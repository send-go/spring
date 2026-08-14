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

## 변경 사항

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
