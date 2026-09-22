# sendgo-spring

> **Spring Boot에서 카카오 알림톡, 브랜드메시지, SMS를 가장 쉽게 발송하는 공식 Spring Boot Starter**

[![Maven Central](https://img.shields.io/maven-central/v/io.sendgo/sendgo-spring)](https://central.sonatype.com/artifact/io.sendgo/sendgo-spring)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=spring)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk)](https://openjdk.org)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

`sendgo-spring`은 [`sendgo-java`](https://github.com/send-go/java) 코어를 확장한 **Spring Boot 전용 스타터**입니다.
`application.yml` 설정만으로 `SendgoClient` 빈이 자동 등록됩니다.

---

## 설치

### Maven

```xml
<dependency>
    <groupId>io.sendgo</groupId>
    <artifactId>sendgo-spring</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Gradle

```groovy
implementation 'io.sendgo:sendgo-spring:1.0.0'
```

---

## 빠른 시작 (3단계)

### 1단계 — application.yml 설정

```yaml
sendgo:
  access-key: ${SENDGO_ACCESS_KEY}
  secret-key: ${SENDGO_SECRET_KEY}
  kakao-sender-key: ${SENDGO_KAKAO_SENDER_KEY}
  sms-sender-key: ${SENDGO_SMS_SENDER_KEY}
  api-version: v2
```

### 2단계 — 서비스에서 SendgoClient 주입

```java
@Service
public class NotificationService {

    private final SendgoClient sendgo;

    public NotificationService(SendgoClient sendgo) {
        this.sendgo = sendgo;
    }

    public void sendOrderConfirm(String phone, String orderNo, String amount) {
        sendgo.alimtalk().send(AlimtalkRequest.builder()
            .templateCode("ORDER_CONFIRM_001")
            .contacts(List.of(
                Contact.builder()
                    .contact(phone)
                    .var1(orderNo)
                    .var2(amount)
                    .build()
            ))
            .build());
    }
}
```

### 3단계 — 컨트롤러에서 사용

```java
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final NotificationService notificationService;

    public OrderController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Boolean>> confirmOrder(@PathVariable Long id) {
        Order order = orderService.findById(id);
        notificationService.sendOrderConfirm(
            order.getPhone(), order.getNumber(), order.getFormattedAmount()
        );
        return ResponseEntity.ok(Map.of("success", true));
    }
}
```

---

## 알림톡 상세 사용법

```java
import io.sendgo.*;
import io.sendgo.model.*;
import java.util.List;

@Service
public class AlimtalkExamples {

    private final SendgoClient sendgo;

    public AlimtalkExamples(SendgoClient sendgo) {
        this.sendgo = sendgo;
    }

    // 다건 발송
    public void sendBulk() {
        sendgo.alimtalk().send(AlimtalkRequest.builder()
            .templateCode("ORDER_CONFIRM_001")
            .contacts(List.of(
                Contact.builder().contact("01011111111").name("홍길동").var1("ORD-001").var2("29,000원").build(),
                Contact.builder().contact("01022222222").name("김철수").var1("ORD-002").var2("15,000원").build(),
                Contact.builder().contact("01033333333").name("이영희").var1("ORD-003").var2("52,000원").build()
            ))
            .build());
    }

    // 예약 발송
    public void sendScheduled() {
        sendgo.alimtalk().send(AlimtalkRequest.builder()
            .templateCode("PROMO_SUMMER_2026")
            .scheduleType("SCHEDULED")
            .at("2026-07-28 09:00:00")
            .contacts(List.of(
                Contact.builder().contact("01012345678").var1("여름 한정 50% 할인").build()
            ))
            .build());
    }

    // SMS 자동 대체 발송
    public void sendWithFallback() {
        sendgo.alimtalk().send(AlimtalkRequest.builder()
            .templateCode("DELIVERY_START_001")
            .replaceSms("Y")
            .smsSubject("[배송 시작 안내]")
            .smsContent("주문하신 상품이 출고되었습니다.\n송장번호: #{var2}")
            .contacts(List.of(
                Contact.builder().contact("01012345678").var1("ORD-001").var2("1234567890").build()
            ))
            .build());
    }
}
```

---

## 친구톡 사용법

> ⚠️ **Deprecated — 친구톡은 카카오 정책에 따라 2025-12-31 종료되었습니다.**
> 2026-01-01 부터 친구톡 발송 요청은 카카오 측에서 **브랜드메시지(자유형)** 로 자동 대체 발송됩니다.
> 호출은 계속 성공하며, 자유 본문 타입(`FT`/`FI`/`FW`)을 개별 수신자에게 보내는 경로는
> 현재 이것뿐이므로 기존 코드를 당장 바꿀 필요는 없습니다.
>
> 다음의 경우에는 **브랜드메시지**를 사용하세요.
> - 템플릿 기반 리치 타입 (`FL`/`FC`/`FM`/`FP`/`FA`)
> - 채널 친구가 **아닌** 수신자 (`targeting` = `N` / `I`)
> - 수신 동의한 전체 채널 친구 동보 (`targeting` = `F`)
>
> 메시지 타입은 1:1 대응되며 변환은 서버가 처리합니다 — `FT`→`BT`, `FI`→`BI`, `FW`→`BW`,
> `FL`→`BL`, `FC`→`BC`, `FM`→`BM`, `FP`→`BP`, `FA`→`BA`.

```java
@Service
public class FriendtalkExamples {

    private final SendgoClient sendgo;

    public FriendtalkExamples(SendgoClient sendgo) { this.sendgo = sendgo; }

    // 텍스트형
    public void sendText() {
        sendgo.friendtalk().send(FriendtalkRequest.builder()
            .content("안녕하세요! 7월 한정 특가 이벤트를 확인해보세요.")
            .contacts(List.of(Contact.builder().contact("01012345678").build()))
            .build());
    }

    // 이미지형
    public void sendImage() {
        sendgo.friendtalk().send(FriendtalkRequest.builder()
            .messageType("FI")
            .content("이번 주 특가 상품을 확인하세요!")
            .imageUrl("https://cdn.example.com/banner.jpg")
            .imageLink("https://example.com/event")
            .contacts(List.of(Contact.builder().contact("01012345678").build()))
            .build());
    }
}
```

---

## SMS / LMS / MMS 사용법

```java
@Service
public class SmsExamples {

    private final SendgoClient sendgo;

    public SmsExamples(SendgoClient sendgo) { this.sendgo = sendgo; }

    // SMS (90자 이하)
    public void sendSms(String phone, String code) {
        sendgo.sms().sendSms(SmsRequest.sms()
            .content("[Sendgo] 인증번호: " + code + " (5분 이내 입력)")
            .contact(Contact.builder().contact(phone).build()));
    }

    // LMS (장문)
    public void sendLms(String phone) {
        sendgo.sms().sendLms(SmsRequest.lms()
            .subject("[중요] 서비스 점검 안내")
            .content("안녕하세요. 서비스 점검이 예정되어 있습니다.\n\n■ 일시: 2026-07-25 02:00 ~ 06:00\n■ 영향: 전체 서비스")
            .contact(Contact.builder().contact(phone).build()));
    }

    // MMS (이미지)
    public void sendMms(List<String> phones) {
        List<Contact> contacts = phones.stream()
            .map(p -> Contact.builder().contact(p).build())
            .toList();

        sendgo.sms().sendMms(SmsRequest.mms()
            .subject("[이벤트] 7월 특가")
            .content("이번 달 특가 상품을 확인하세요!")
            .contacts(contacts));
    }
}
```

---

## Spring Events 통합

```java
// 이벤트 정의
public record OrderConfirmedEvent(String phone, String orderNo, String amount) {}

// 이벤트 리스너
@Component
public class OrderEventListener {

    private final SendgoClient sendgo;

    public OrderEventListener(SendgoClient sendgo) { this.sendgo = sendgo; }

    @EventListener
    @Async
    public void handleOrderConfirmed(OrderConfirmedEvent event) {
        sendgo.alimtalk().send(AlimtalkRequest.builder()
            .templateCode("ORDER_CONFIRM_001")
            .contacts(List.of(
                Contact.builder()
                    .contact(event.phone())
                    .var1(event.orderNo())
                    .var2(event.amount())
                    .build()
            ))
            .build());
    }
}

// 이벤트 발행
@Service
public class OrderService {

    private final ApplicationEventPublisher publisher;

    public void confirmOrder(Order order) {
        // ... 주문 처리 로직
        publisher.publishEvent(new OrderConfirmedEvent(
            order.getPhone(), order.getNumber(), order.getFormattedAmount()
        ));
    }
}
```

---

## 예외 처리

```java
import io.sendgo.exception.SendgoException;

@Service
public class SafeNotificationService {

    private final SendgoClient sendgo;
    private static final Logger log = LoggerFactory.getLogger(SafeNotificationService.class);

    public SafeNotificationService(SendgoClient sendgo) { this.sendgo = sendgo; }

    public void sendSafely(String templateCode, String phone, String var1) {
        try {
            sendgo.alimtalk().send(AlimtalkRequest.builder()
                .templateCode(templateCode)
                .contact(Contact.builder().contact(phone).var1(var1).build())
                .build());
        } catch (SendgoException e) {
            log.error("알림톡 발송 실패: HTTP {} [{}] endpoint={}",
                e.getStatusCode(), e.getErrorCode(), e.getEndpoint());

            switch (e.getErrorCode()) {
                case "INVALID_ACCESS_KEY",
                     "INVALID_SECRET_KEY"   -> alertOps("Sendgo 인증키 오류");
                case "INVALID_TEMPLATE_CODE" -> log.warn("존재하지 않는 템플릿: {}", templateCode);
                case "PAYMENT_REQUIRED"      -> alertOps("Sendgo 크레딧 부족");
                case "IP_NOT_ALLOWED"        -> alertOps("허용되지 않은 IP");
                default                      -> log.error("알 수 없는 오류", e);
            }
        }
    }
}
```

---

## 설정 옵션 (application.yml)

| 키 | 필수 | 기본값 | 설명 |
|----|------|--------|------|
| `sendgo.access-key` | **필수** | — | Sendgo 액세스 키 |
| `sendgo.secret-key` | **필수** | — | Sendgo 시크릿 키 |
| `sendgo.kakao-sender-key` | 선택 | `null` | 카카오 발신프로필 키 |
| `sendgo.sms-sender-key` | 선택 | `null` | SMS 발신자 키 |
| `sendgo.api-version` | 선택 | `v2` | API 버전 |
| `sendgo.url` | 선택 | `https://sendgo.io` | API 기본 URL |

---

## 관련 패키지

| 언어/프레임워크 | 패키지 | GitHub |
|----------------|--------|--------|
| Java (순수) | `io.sendgo:sendgo-java` | [java](https://github.com/send-go/java) |
| Laravel | `sendgo/laravel` | [laravel](https://github.com/send-go/laravel) |
| Node.js | `@sendgo/node` | [node](https://github.com/send-go/node) |
| Python | `sendgo-python` | [python](https://github.com/send-go/python) |
| 전체 목록 | — | [send-go GitHub 조직](https://github.com/send-go) |

---

## 브랜드메시지 · 짧은 URL

이 패키지는 코어(`io.sendgo:sendgo-java`)의 클라이언트를 그대로 노출하므로, 코어에 있는 채널이
모두 그대로 쓸 수 있습니다. 두 기능 모두 **v2 전용**입니다.

| 기능 | 접근 |
|------|------|
| 카카오 브랜드메시지 (친구톡의 후속 채널) | `sendgo.brandMessage()` |
| 짧은 URL (단축 + 클릭 반응 분석) | `sendgo.shortUrl()` |

브랜드메시지는 채널 친구가 아닌 수신자에게도 보낼 수 있고(`targeting` = `N`),
수신 동의한 전체 채널 친구에게 동보 발송할 수도 있습니다(`targeting` = `F`).

짧은 URL 은 메시지 본문의 링크를 줄이고 클릭 반응(일별 추이·디바이스·유입경로·국가)을
집계합니다.

사용 예시와 파라미터는 [코어 README](https://github.com/send-go) 와
[SDK 가이드](https://sendgo.io/ko/sdk) 를 참고하세요.

## 관리 API — 채널·템플릿·발신번호 등록 (v2 전용)

자동 구성이 만드는 `SendgoClient` 빈에 코어의 관리 서비스가 그대로 붙어
있습니다. 콘솔에서만 되던 등록·심사를 서비스 빈에서 처리할 수 있습니다.

| 서비스 | 하는 일 | 계정 |
| --- | --- | --- |
| `sendgo.kakaoSenders()` | 카카오 채널 인증·등록·동기화, 브랜드메시지 M/N 신청 | 기업 |
| `sendgo.noticeTemplates()` | 알림톡 템플릿 CRUD, 검수 요청·취소, 승인 취소, 휴면 해제 | 기업 |
| `sendgo.brandTemplates()` | 브랜드메시지(구 친구톡) 템플릿 CRUD, 동기화, 가져오기 | 기업 |
| `sendgo.senderRegistration()` | 발신번호 등록 신청, 중복 확인, 유형 안내 | 개인·기업 |
| `sendgo.messageTemplates()` | 문자 상용구 템플릿 CRUD | 개인·기업 |
| `sendgo.kakaoImages()` | 카카오 이미지 업로드 — 템플릿용 URL 발급 | 기업 |
| `sendgo.rejectedNumbers()` | 수신거부(080) 번호 조회 | 개인·기업 |
| `sendgo.webhook()` | 이벤트 웹훅 구독 — 심사 결과 수신 | 개인·기업 |

> **sendgo.io 콘솔에 들어올 일이 없습니다.** 고객의 채널·발신번호·템플릿을
> 여러분 화면만으로 끝까지 처리할 수 있습니다. 휴대폰 발신번호는 콘솔의 PASS
> 본인인증 대신 **신분증 사본(`identityDocument`)을 받아 sendgo 운영자가 대신
> 심사**합니다.
>
> 사람이 개입하는 지점은 **카카오 채널 인증번호 하나**뿐이고, 그마저도
> 여러분 화면에서 입력받으면 됩니다 — 카카오가 관리자 휴대폰으로 직접 보내는
> 확인이라 없앨 수 없습니다.
>
> 심사가 붙는 것들은 **비동기**입니다. 등록 호출이 성공했다는 건 "접수됐다"는
> 뜻이지 "쓸 수 있다"는 뜻이 아닙니다 — 웹훅을 구독해 결과를 받으세요.

```java
@Service
public class OnboardingService {

    private static final Logger log = LoggerFactory.getLogger(OnboardingService.class);

    private final SendgoClient sendgo;

    public OnboardingService(SendgoClient sendgo) {
        this.sendgo = sendgo;
    }

    /** 1단계 — 카카오가 관리자 휴대폰으로 인증번호를 SMS 발송한다. */
    public void requestChannelCode(String yellowId, String phone) {
        sendgo.kakaoSenders().requestToken(yellowId, phone);
    }

    /** 2단계 — 사용자가 입력한 인증번호로 발신프로필 생성. */
    public String completeChannel(String yellowId, String phone, String code) {
        Map<String, Object> created = sendgo.kakaoSenders().create(
                KakaoSenderCreateRequest.builder()
                        .token(code)
                        .yellowId(yellowId)
                        .phoneNumber(phone)
                        .categoryCode("001001")
                        .build());

        return ((Map<?, ?>) ((Map<?, ?>) created.get("data")).get("sender"))
                .get("kakaoSenderKey").toString();
    }

    /** 표준 템플릿을 등록하고 검수를 요청한다. */
    public String provisionTemplate(String kakaoSenderKey) {
        Map<String, Object> created = sendgo.noticeTemplates().create(
                NoticeTemplateRequest.builder()
                        .kakaoSenderKey(kakaoSenderKey)
                        .templateName("주문 접수 안내")
                        .templateContent("#{name}님, 주문 #{orderNo}이 접수되었습니다.")
                        .categoryCode("001001")
                        .messagePurpose("order_delivery")
                        .legalBasis("transaction")
                        .benefitOrigin("none")
                        .expiryType("none")
                        .build());

        String code = ((Map<?, ?>) ((Map<?, ?>) created.get("data")).get("template"))
                .get("templateCode").toString();

        sendgo.noticeTemplates().requestInspection(code);

        return code;
    }

    /**
     * 승인 여부는 스케줄러로 확인한다. 검수는 30분~1영업일 걸리므로
     * 요청 스레드에서 기다리면 안 된다.
     */
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES)
    public void pollPendingTemplates() {
        for (String code : pendingTemplateCodes()) {
            Map<String, Object> synced = sendgo.noticeTemplates().sync(code);
            Object status = ((Map<?, ?>) ((Map<?, ?>) synced.get("data")).get("template"))
                    .get("inspectionStatus");

            if ("APR".equals(status)) log.info("템플릿 승인: {}", code);
            if ("REJ".equals(status)) log.warn("템플릿 반려: {}", code);
        }
    }

    private List<String> pendingTemplateCodes() {
        return List.of();  // 앱의 저장소에서 읽어 온다
    }
}
```

발신번호 등록과 브랜드메시지 템플릿을 포함한 전체 파라미터는
[sendgo-java README](https://github.com/send-go/java) 를 참고하세요.

---

## 변경 사항

### 1.3.0 (2026-09-11)

- **관리 API 노출** — 코어 1.3.0 의 `kakaoSenders()` · `noticeTemplates()` ·
  `brandTemplates()` · `senderRegistration()` · `messageTemplates()` 를
  자동 구성된 `SendgoClient` 빈에서 그대로 쓸 수 있습니다. 콘솔에서만 되던
  채널 등록, 알림톡 템플릿 검수 요청, 발신번호 심사 접수를 처리합니다.
- `io.sendgo:sendgo-java` 의존성을 `1.3.0` 으로 올렸습니다.
- `CoreSurfaceTest` 가 관리 API 표면까지 컴파일로 확인합니다 — 코어에 서비스가
  늘었는데 스타터의 의존성 버전을 올리지 않으면 여기서 빌드가 깨집니다.
- **이벤트 웹훅** 추가 — 발신번호 승인, 알림톡 검수 결과, 채널 차단,
  브랜드메시지 타겟팅 결과를 구독해 받습니다. 서명은 받은 원본 바이트로
  검증합니다(SDK 에 검증 헬퍼 포함).
- **카카오 이미지 업로드** 추가 — 브랜드메시지 템플릿의 `imageUrl` 은 카카오가
  호스팅하는 URL 이어야 하는데, 그 URL 을 얻는 길이 콘솔에만 있었습니다.
- **수신거부(080) 조회** 추가 — 자기 DB 의 수신 상태를 맞출 수 있습니다.

### 1.2.1 (2026-08-14)

- 레지스트리 목록에 노출되는 패키지 설명에서 친구톡을 브랜드메시지로 교체했습니다.
  npm/PyPI/Packagist/Maven/NuGet/RubyGems 검색 결과에 그대로 찍히는 문자열이라
  종료된 채널을 계속 홍보하고 있었습니다.
- 검색 키워드에 `brand-message` 를 추가했습니다 (`friendtalk` 은 유입 검색어라 유지).

### 1.2.0 (2026-08-14)

- **친구톡 Deprecated 표기** — 친구톡은 카카오 정책에 따라 2025-12-31 종료되었고,
  2026-01-01 부터 발송 요청이 브랜드메시지(자유형)로 자동 대체 발송됩니다.
  관련 API 에 각 언어의 표준 deprecation 표기를 달았습니다.
- 자유 본문 타입(`FT`/`FI`/`FW`)의 개별 발송 경로는 아직 친구톡 API 뿐이라는 점을
  문서에 명시했습니다 — 브랜드메시지 API 는 그 조합에 `NOT_A_BRAND_MESSAGE` 를 반환합니다.
- 브랜드메시지 전환 안내와 메시지 타입 1:1 대응표를 README 에 추가했습니다.

### 1.1.0 (2026-08-11)

- **`sendgo-java` 의존을 1.1.0 으로 올림** — 1.0.1 로 고정돼 있어 Spring 사용자에게 `shortUrl()` 이 노출되지 않았다.

## 라이선스

MIT License © 2026 [Sendgo](https://sendgo.io)

---

*키워드: 카카오 알림톡 Spring Boot, 카카오 친구톡 Spring Boot, SMS 발송 Spring Boot, 알림톡 Spring Starter, Spring Boot 카카오 API, Sendgo Spring SDK, Spring Boot 알림 발송*

## 계정 API (1.4.0)

코어 1.4.0의 계정·조직·API 키·허용 IP 관리 12개 API를 사용할 수 있습니다.
발송용 키 없이 에이전트 토큰만으로 구성할 수 있습니다.

발송용 `accessKey`/`secretKey`가 없는 단계에서 사용하는 **별도 계정 클라이언트**입니다.
콘솔에서 발급받은 에이전트 토큰(`SENDGO_AGENT_TOKEN`)으로 `/api/v2/account`를 호출합니다.
계정 조회에는 `account:read`, 키·허용 IP 변경에는 `keys:write` 권한이 필요합니다.
토큰 만료나 권한 부족(401/403)은 그대로 예외로 반환하며 자동 갱신·재시도하지 않습니다.

조직 선택은 서버에 저장되는 **사용자 계정의 현재 조직**을 바꿉니다. 같은 사용자로
여러 조직의 설정을 동시에 변경하지 마세요. 개인 계정으로 돌아가려면 조직 ID에
`null`(Python `None`, Ruby `nil`, Go `nil`) 또는 `personal`을 전달합니다.
키 발급 응답의 `data.apiKey.secretKey`는 한 번만 반환되므로 서버의 비밀 저장소에 보관하세요.
허용 IP가 하나라도 등록되면 목록 밖의 IP는 차단됩니다.
에이전트 토큰과 키는 브라우저·모바일 앱에 포함하거나 응답·로그에 출력하지 않습니다.

```yaml
sendgo:
  agent-token: ${SENDGO_AGENT_TOKEN}
# io.sendgo.AccountClient 빈이 등록됩니다. 발송용 키는 필요하지 않습니다.
```
