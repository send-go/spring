# sendgo-spring-boot-starter

> **Sendgo** Spring Boot Auto-Configuration Starter
> 카카오 알림톡(Alimtalk), 친구톡(Friendtalk), SMS/LMS/MMS를 Spring Boot에서 간편하게 사용하세요.

[![Maven Central](https://img.shields.io/maven-central/v/io.sendgo/sendgo-spring-boot-starter)](https://central.sonatype.com/artifact/io.sendgo/sendgo-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-17+-orange)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green)](https://spring.io/projects/spring-boot)

---

## 빠른 시작 (3단계)

### 1단계 — 의존성 추가

**Gradle (build.gradle.kts)**
```kotlin
dependencies {
    implementation("io.sendgo:sendgo-spring-boot-starter:1.0.0")
}
```

**Maven (pom.xml)**
```xml
<dependency>
    <groupId>io.sendgo</groupId>
    <artifactId>sendgo-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

### 2단계 — application.yml 설정

```yaml
sendgo:
  access-key: ${SENDGO_ACCESS_KEY}        # Sendgo 액세스 키 (필수)
  secret-key: ${SENDGO_SECRET_KEY}        # Sendgo 시크릿 키 (필수)
  kakao-sender-key: ${SENDGO_KAKAO_KEY}  # 카카오 발신프로필 키 (알림톡/친구톡 사용 시)
  sms-sender-key: ${SENDGO_SMS_KEY}      # SMS 발신자 키
  api-version: v2                         # API 버전 (v1 | v2, 기본값: v1)
```

> **발신프로필 키 발급 방법**
> [Sendgo 콘솔](https://sendgo.io) → 카카오 발신프로필 → 발신프로필 등록 → 채널 연결 후 키 복사

---

### 3단계 — 알림톡 전송

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final SendgoClient sendgo;

    public void sendOrderConfirm(String phone, String orderNumber) {
        sendgo.sendAlimtalk(
            AlimtalkRequest.builder()
                .templateCode("ORDER_CONFIRM_001")  // SendGo 승인 템플릿 코드
                .contact(Contact.builder()
                    .contact(phone)          // 수신자 전화번호
                    .var1(orderNumber)       // 템플릿 변수 #{var1}
                    .build())
                .build()
        );
    }
}
```

---

## 기능별 사용법

### 카카오 알림톡 (Alimtalk)

#### 기본 전송

```java
sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("ORDER_CONFIRM_001")
        .contact(Contact.builder()
            .contact("01012345678")
            .name("홍길동")
            .var1("ORD-20260101-001")  // 주문번호
            .var2("스프링 입문서")      // 상품명
            .var3("29,000원")          // 금액
            .build())
        .build()
);
```

#### 다건 발송

```java
List<Contact> recipients = List.of(
    Contact.builder().contact("01011111111").var1("ORD-001").build(),
    Contact.builder().contact("01022222222").var1("ORD-002").build()
);

sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("ORDER_CONFIRM_001")
        .contacts(recipients)
        .build()
);
```

#### 예약 발송

```java
sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("PROMO_001")
        .scheduleType("SCHEDULED")
        .at("2026-04-01 09:00:00")  // 예약 발송 시각
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

#### 알림톡 실패 시 SMS 대체 발송

```java
sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("DELIVERY_001")
        .replaceSms("[배송 안내]", "주문하신 상품이 출고되었습니다.")  // 대체 SMS 제목, 내용
        .contact(Contact.builder()
            .contact("01012345678")
            .var1("ORD-001")
            .var2("1234567890")
            .build())
        .build()
);
```

---

### 카카오 친구톡 (Friendtalk)

```java
sendgo.sendFriendtalk(
    FriendtalkRequest.builder()
        .content("안녕하세요! 봄맞이 30% 할인 이벤트가 시작되었습니다.")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

#### 이미지 포함 친구톡

```java
sendgo.sendFriendtalk(
    FriendtalkRequest.builder()
        .messageType("FI")  // 이미지형
        .content("이번 주 특가 상품을 확인하세요!")
        .imageUrl("https://example.com/event-banner.jpg")
        .imageLink("https://example.com/event")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

---

### SMS / LMS / MMS

```java
// SMS (단문, 90자 이하)
sendgo.sendSms(
    SmsRequest.sms()
        .content("인증번호: 123456")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);

// LMS (장문, 2000자 이하)
sendgo.sendLms(
    SmsRequest.lms()
        .subject("[공지] 서비스 점검 안내")
        .content("안녕하세요.\n\n서비스 점검이 예정되어 있습니다.\n일시: 2026-04-01 02:00 ~ 06:00")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);

// MMS (멀티미디어)
sendgo.sendMms(
    SmsRequest.mms()
        .subject("[이벤트] 봄 특가")
        .content("이번 주 특가 상품을 확인하세요!")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

---

## 예외 처리

```java
import io.sendgo.starter.exception.SendgoException;

try {
    sendgo.sendAlimtalk(request);
} catch (SendgoException e) {
    log.error("알림톡 발송 실패: status={}, errorCode={}, endpoint={}",
        e.getStatusCode(), e.getErrorCode(), e.getEndpoint());

    switch (e.getErrorCode()) {
        case "INVALID_ACCESS_KEY", "INVALID_SECRET_KEY" ->
            log.error("인증키를 확인하세요.");
        case "INVALID_TEMPLATE_CODE" ->
            log.error("템플릿 코드를 확인하세요.");
        case "PAYMENT_REQUIRED" ->
            log.error("크레딧이 부족합니다.");
        case "EMPTY_CONTACTS" ->
            log.error("수신자 정보를 확인하세요.");
        default ->
            log.error("알 수 없는 오류: {}", e.getMessage());
    }
}
```

---

## v2 API 사용법

`application.yml`에서 버전만 변경하면 코드 수정 없이 v2 API를 사용할 수 있습니다.

```yaml
sendgo:
  api-version: v2
```

v2는 에러 코드가 세분화되어 있으며, 아래 코드는 토큰 재발급 없이 즉시 예외를 발생시킵니다:

| 에러 코드 | 설명 |
|-----------|------|
| `INVALID_ACCESS_KEY` | 잘못된 액세스 키 |
| `INVALID_SECRET_KEY` | 잘못된 시크릿 키 |
| `ACCESS_KEY_NOT_APPROVED` | 미승인 앱 |
| `IP_NOT_ALLOWED` | 허용되지 않은 IP |
| `INVALID_SENDER_KEY` | 잘못된 SMS 발신키 |
| `INVALID_KAKAO_SENDER_KEY` | 잘못된 카카오 발신키 |

전체 에러 코드 목록은 [API_V2_ERROR_CODES.md](../sendgo-notification/API_V2_ERROR_CODES.md)를 참고하세요.

---

## Spring Bean 자동 등록

`sendgo.access-key`가 설정되면 `SendgoClient`가 자동으로 Bean으로 등록됩니다.
`@Autowired` 또는 생성자 주입으로 사용하세요.

```java
@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final SendgoClient sendgo;

    @PostMapping("/notify")
    public ResponseEntity<Void> notify(@RequestBody NotifyRequest req) {
        sendgo.sendAlimtalk(
            AlimtalkRequest.builder()
                .templateCode("NOTIFY_001")
                .contact(Contact.builder().contact(req.phone()).build())
                .build()
        );
        return ResponseEntity.ok().build();
    }
}
```

---

## 설정 전체 옵션

| 키 | 설명 | 기본값 |
|----|------|--------|
| `sendgo.url` | API 기본 URL | `https://api.sendgo.io` |
| `sendgo.access-key` | 액세스 키 (필수) | — |
| `sendgo.secret-key` | 시크릿 키 (필수) | — |
| `sendgo.kakao-sender-key` | 카카오 발신프로필 키 | — |
| `sendgo.sms-sender-key` | SMS 발신자 키 | — |
| `sendgo.api-version` | API 버전 (`v1` \| `v2`) | `v1` |

---

## 라이선스

MIT License © [Sendgo](https://sendgo.io)
