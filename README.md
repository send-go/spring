# sendgo-spring-boot-starter

> **Spring Boot에서 카카오 알림톡, 친구톡, SMS를 가장 쉽게 연동하는 방법**

[![Maven Central](https://img.shields.io/maven-central/v/io.sendgo/sendgo-spring-boot-starter?label=Maven%20Central)](https://central.sonatype.com/artifact/io.sendgo/sendgo-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-17%2B-orange?logo=openjdk)](https://openjdk.org)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

`sendgo-spring-boot-starter`는 [Sendgo](https://sendgo.io) 알림 API를 Spring Boot 애플리케이션에 **Auto-Configuration** 방식으로 즉시 통합할 수 있는 공식 스타터입니다.
`application.yml` 설정 몇 줄만으로 **카카오 알림톡(Alimtalk), 친구톡(Friendtalk), SMS/LMS/MMS** 발송 기능을 Bean으로 주입받아 사용할 수 있습니다.

---

## 목차

- [Sendgo란?](#sendgo란)
- [주요 기능](#주요-기능)
- [지원 메시지 유형](#지원-메시지-유형)
- [빠른 시작](#빠른-시작)
- [상세 사용법](#상세-사용법)
  - [카카오 알림톡](#카카오-알림톡-alimtalk)
  - [카카오 친구톡](#카카오-친구톡-friendtalk)
  - [SMS / LMS / MMS](#sms--lms--mms)
- [Spring Bean 자동 등록](#spring-bean-자동-등록)
- [설정 옵션](#설정-옵션-전체)
- [예외 처리](#예외-처리)
- [v2 API](#v2-api-사용법)
- [자주 묻는 질문](#자주-묻는-질문-faq)
- [관련 패키지](#관련-패키지)

---

## Sendgo란?

[Sendgo](https://sendgo.io)는 대한민국 기업과 개발자를 위한 **통합 알림 발송 플랫폼**입니다.

- **카카오 알림톡**: 카카오톡 채널을 통한 정보성 메시지 발송 (주문 확인, 배송 안내, 인증번호 등)
- **카카오 친구톡**: 카카오톡 채널 친구에게 마케팅 메시지 발송 (이벤트, 프로모션 등)
- **SMS/LMS/MMS**: 전통적인 문자 메시지 발송
- **자동 대체 발송**: 알림톡 실패 시 SMS로 자동 전환

국내 이커머스, 핀테크, 헬스케어, 교육 플랫폼 등 다양한 산업에서 사용되고 있으며, **Spring Boot 기반의 국내 백엔드 서버**에서 가장 많이 사용하는 알림 발송 SDK입니다.

---

## 주요 기능

| 기능 | 설명 |
|------|------|
| **Auto-Configuration** | `application.yml` 설정만으로 자동 Bean 등록 |
| **`@EnableSendgo` 어노테이션** | 명시적 활성화 지원 |
| **토큰 자동 관리** | 발급·갱신·캐시(50분)를 SDK가 자동 처리 |
| **401/403 자동 재시도** | 토큰 만료 시 1회 자동 갱신 후 재발송 |
| **다건 동시 발송** | 수신자 목록(List) 전달로 대량 발송 지원 |
| **예약 발송** | 원하는 시각에 예약 발송 (`scheduleType: SCHEDULED`) |
| **SMS 자동 대체 발송** | 알림톡 실패 시 SMS로 자동 전환 |
| **v1 / v2 API 지원** | 설정 한 줄로 API 버전 전환 |
| **타입 안전 빌더** | Java 빌더 패턴으로 컴파일 타임 오류 방지 |

---

## 지원 메시지 유형

### 카카오 알림톡 (Alimtalk)
카카오 비즈니스 채널에서 **사전 승인된 템플릿**을 기반으로 정보성 메시지를 발송합니다.
- 템플릿 변수 `#{var1}` ~ `#{var8}` 지원
- 알림톡 실패 시 SMS 자동 대체 발송 (`replaceSms: Y`)
- 즉시 발송 / 예약 발송 모두 지원

### 카카오 친구톡 (Friendtalk)
카카오톡 채널을 추가한 사용자에게 **광고성/정보성 메시지**를 자유 형식으로 발송합니다.
- 텍스트형(FT), 이미지형(FI), 와이드 이미지(FW), 리스트형(FL) 등 8가지 유형
- 이미지, 버튼, 링크 첨부 가능

### SMS / LMS / MMS
- **SMS**: 단문 문자 (90바이트 이하)
- **LMS**: 장문 문자 (2,000바이트 이하), 제목 포함
- **MMS**: 멀티미디어 문자 (이미지 첨부)
- 광고성 / 일반 / 선거 캠페인 유형 지원

---

## 빠른 시작

### 1단계 — 의존성 추가

**Gradle (Kotlin DSL)**
```kotlin
dependencies {
    implementation("io.sendgo:sendgo-spring-boot-starter:1.0.0")
}
```

**Maven**
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
  access-key: ${SENDGO_ACCESS_KEY}       # Sendgo 액세스 키 (필수)
  secret-key: ${SENDGO_SECRET_KEY}       # Sendgo 시크릿 키 (필수)
  kakao-sender-key: ${SENDGO_KAKAO_KEY}  # 카카오 발신프로필 키 (알림톡/친구톡 사용 시)
  sms-sender-key: ${SENDGO_SMS_KEY}      # SMS 발신자 키
  api-version: v2                         # API 버전 (v1 | v2, 기본값: v1)
```

> **카카오 발신프로필 키 발급 방법**
> 1. [Sendgo 콘솔](https://sendgo.io) 로그인
> 2. `카카오 발신프로필` 메뉴 → 발신프로필 등록
> 3. 카카오 채널 검색 후 연결 승인
> 4. 발급된 `발신프로필 키(kakaoSenderKey)` 복사

---

### 3단계 — 알림톡 전송

```java
@Service
@RequiredArgsConstructor
public class OrderService {

    private final SendgoClient sendgo;  // Auto-Configuration으로 자동 주입

    public void sendOrderConfirm(String phone, String orderNumber) {
        sendgo.sendAlimtalk(
            AlimtalkRequest.builder()
                .templateCode("ORDER_CONFIRM_001")  // Sendgo에서 승인받은 템플릿 코드
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

## 상세 사용법

### 카카오 알림톡 (Alimtalk)

#### 단건 발송 (즉시)

```java
sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("ORDER_CONFIRM_001")
        .contact(Contact.builder()
            .contact("01012345678")
            .name("홍길동")
            .var1("ORD-20260723-001")  // 주문번호
            .var2("스프링 부트 완벽 가이드")  // 상품명
            .var3("29,000원")           // 결제금액
            .var4("2026-07-24")         // 배송 예정일
            .build())
        .build()
);
```

#### 다건 발송 (대량 발송)

```java
List<Contact> recipients = List.of(
    Contact.builder().contact("01011111111").name("홍길동").var1("ORD-001").build(),
    Contact.builder().contact("01022222222").name("김철수").var1("ORD-002").build(),
    Contact.builder().contact("01033333333").name("이영희").var1("ORD-003").build()
);

sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("ORDER_CONFIRM_001")
        .contacts(recipients)
        .build()
);
```

#### 알림톡 실패 시 SMS 자동 대체 발송

```java
// 알림톡 발송 실패 시 자동으로 SMS로 전환됩니다.
sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("DELIVERY_START_001")
        .replaceSms("[배송 시작]", "주문하신 상품이 출고되었습니다.\n송장번호: #{var2}")
        .contact(Contact.builder()
            .contact("01012345678")
            .var1("ORD-001")
            .var2("1234567890")  // 송장번호
            .build())
        .build()
);
```

#### 예약 발송

```java
sendgo.sendAlimtalk(
    AlimtalkRequest.builder()
        .templateCode("PROMO_SPRING_2026")
        .scheduleType("SCHEDULED")
        .at("2026-04-01 09:00:00")  // 발송 예약 시각
        .contact(Contact.builder()
            .contact("01012345678")
            .var1("봄맞이 30% 할인")
            .build())
        .build()
);
```

---

### 카카오 친구톡 (Friendtalk)

#### 텍스트 친구톡

```java
sendgo.sendFriendtalk(
    FriendtalkRequest.builder()
        .content("안녕하세요! 7월 한정 특가 이벤트를 확인해보세요.\n최대 50% 할인 중!")
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
        .imageUrl("https://cdn.example.com/event-banner.jpg")
        .imageLink("https://example.com/event")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

#### 버튼 포함 친구톡

```java
import java.util.List;
import java.util.Map;

sendgo.sendFriendtalk(
    FriendtalkRequest.builder()
        .content("신규 가입 이벤트! 첫 구매 10% 쿠폰을 드립니다.")
        .buttons(List.of(
            Map.of("name", "이벤트 보기", "type", "WL",
                   "linkMo", "https://example.com/event",
                   "linkPc", "https://example.com/event")
        ))
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

---

### SMS / LMS / MMS

```java
// SMS (단문, 90자 이하) — 인증번호, 간단 안내
sendgo.sendSms(
    SmsRequest.sms()
        .content("[Sendgo] 인증번호: 123456 (5분 이내 입력)")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);

// LMS (장문, 2,000자 이하) — 공지사항, 상세 안내
sendgo.sendLms(
    SmsRequest.lms()
        .subject("[중요] 서비스 점검 안내")
        .content("""
            안녕하세요. 서비스 점검이 예정되어 있습니다.

            ■ 점검 일시: 2026-07-25 (토) 02:00 ~ 06:00 (4시간)
            ■ 점검 내용: 서버 인프라 업그레이드
            ■ 영향 범위: 전체 서비스

            이용에 불편을 드려 죄송합니다.
            """)
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);

// MMS (멀티미디어) — 이미지 포함 문자
sendgo.sendMms(
    SmsRequest.mms()
        .subject("[이벤트] 7월 특가")
        .content("이번 달 특가 상품을 확인하세요!")
        .contact(Contact.builder().contact("01012345678").build())
        .build()
);
```

---

## Spring Bean 자동 등록

`sendgo.access-key`가 `application.yml`에 설정되면 `SendgoClient`가 **자동으로 Spring Bean으로 등록**됩니다.
별도의 `@Configuration` 없이 `@Autowired` 또는 생성자 주입으로 바로 사용할 수 있습니다.

```java
// 방법 1: 생성자 주입 (권장)
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SendgoClient sendgo;
}

// 방법 2: @Autowired
@Service
public class NotificationService {
    @Autowired
    private SendgoClient sendgo;
}
```

### `@EnableSendgo` 어노테이션

Auto-Configuration을 사용하지 않고 명시적으로 활성화할 수 있습니다.

```java
@SpringBootApplication
@EnableSendgo
public class MyApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyApplication.class, args);
    }
}
```

---

## 설정 옵션 전체

| 키 | 타입 | 필수 | 기본값 | 설명 |
|----|------|------|--------|------|
| `sendgo.url` | `String` | 아니오 | `https://api.sendgo.io` | Sendgo API 기본 URL |
| `sendgo.access-key` | `String` | **필수** | — | Sendgo 액세스 키 |
| `sendgo.secret-key` | `String` | **필수** | — | Sendgo 시크릿 키 |
| `sendgo.kakao-sender-key` | `String` | 선택 | — | 카카오 발신프로필 키 (알림톡/친구톡 필수) |
| `sendgo.sms-sender-key` | `String` | 선택 | — | SMS 발신자 키 |
| `sendgo.api-version` | `String` | 아니오 | `v1` | API 버전 (`v1` \| `v2`) |

### 환경별 설정 예시

```yaml
# application.yml (공통)
sendgo:
  api-version: v2

# application-prod.yml (운영)
sendgo:
  access-key: ${SENDGO_ACCESS_KEY}
  secret-key: ${SENDGO_SECRET_KEY}
  kakao-sender-key: ${SENDGO_KAKAO_KEY}
  sms-sender-key: ${SENDGO_SMS_KEY}

# application-dev.yml (개발)
sendgo:
  access-key: dev_access_key
  secret-key: dev_secret_key
```

---

## 예외 처리

```java
import io.sendgo.starter.exception.SendgoException;

try {
    sendgo.sendAlimtalk(request);
} catch (SendgoException e) {
    log.error("알림톡 발송 실패: status={}, code={}, endpoint={}",
        e.getStatusCode(), e.getErrorCode(), e.getEndpoint());

    switch (e.getErrorCode()) {
        // 인증 오류
        case "INVALID_ACCESS_KEY", "INVALID_SECRET_KEY" ->
            alertOps("Sendgo 인증키를 확인하세요.");
        // 발신프로필 오류
        case "INVALID_KAKAO_SENDER_KEY" ->
            alertOps("카카오 발신프로필 키를 확인하세요.");
        // 템플릿 오류
        case "INVALID_TEMPLATE_CODE" ->
            log.warn("템플릿 코드 [{}] 가 존재하지 않습니다.", request.getTemplateCode());
        // 잔액 부족
        case "PAYMENT_REQUIRED" ->
            alertOps("Sendgo 크레딧이 부족합니다. 충전이 필요합니다.");
        // 수신자 오류
        case "EMPTY_CONTACTS" ->
            log.warn("수신자 목록이 비어있습니다.");
        default ->
            log.error("알 수 없는 오류: {}", e.getMessage());
    }
}
```

### 주요 에러 코드 목록

| 에러 코드 | HTTP | 설명 | 해결 방법 |
|-----------|------|------|----------|
| `INVALID_ACCESS_KEY` | 401 | 잘못된 액세스 키 | Sendgo 콘솔에서 키 확인 |
| `INVALID_SECRET_KEY` | 401 | 잘못된 시크릿 키 | Sendgo 콘솔에서 키 확인 |
| `ACCESS_KEY_NOT_APPROVED` | 403 | 미승인 앱 | 앱 승인 요청 |
| `IP_NOT_ALLOWED` | 403 | IP 차단 | 허용 IP 목록 확인 |
| `INVALID_KAKAO_SENDER_KEY` | 403 | 잘못된 카카오 발신키 | 발신프로필 키 확인 |
| `INVALID_TEMPLATE_CODE` | 400 | 존재하지 않는 템플릿 | 템플릿 코드 확인 |
| `PAYMENT_REQUIRED` | 402 | 크레딧 부족 | Sendgo 콘솔에서 충전 |
| `EMPTY_CONTACTS` | 400 | 수신자 없음 | contacts 필드 확인 |

---

## v2 API 사용법

`application.yml`에서 `api-version: v2`로 변경하면 코드 수정 없이 v2 API를 사용할 수 있습니다.

```yaml
sendgo:
  api-version: v2
```

**v2의 특징:**
- Bearer 토큰을 Base64 인코딩 없이 그대로 전송
- 에러 코드가 더 세분화되어 오류 원인 파악이 용이
- 아래 코드는 토큰 갱신 없이 즉시 예외를 발생시킵니다:
  `INVALID_ACCESS_KEY`, `INVALID_SECRET_KEY`, `ACCESS_KEY_NOT_APPROVED`, `IP_NOT_ALLOWED`, `INVALID_SENDER_KEY`, `INVALID_KAKAO_SENDER_KEY`

---

## 자주 묻는 질문 (FAQ)

**Q. Spring Boot 2.x에서도 사용할 수 있나요?**
A. 이 스타터는 Spring Boot 3.x (Java 17+)를 기준으로 개발되었습니다. Spring Boot 2.x는 `AutoConfiguration.imports` 방식이 지원되지 않아 별도 설정이 필요합니다.

**Q. 토큰은 어떻게 관리되나요?**
A. `SendgoClient` 내부에서 인메모리로 토큰을 캐싱합니다(50분). 401/403 응답 시 자동으로 토큰을 재발급하고 1회 재시도합니다. 개발자가 직접 토큰을 관리할 필요가 없습니다.

**Q. 멀티 테넌트 환경에서 여러 Sendgo 계정을 사용하려면?**
A. `@ConditionalOnMissingBean`이 적용되어 있으므로, 직접 `SendgoClient`를 복수로 Bean 등록하여 사용할 수 있습니다.

**Q. 카카오 알림톡 템플릿은 어디서 만드나요?**
A. [Sendgo 콘솔](https://sendgo.io) → 알림톡 템플릿 메뉴에서 템플릿을 작성하고 카카오 심사를 신청합니다. 심사 통과 후 템플릿 코드를 사용할 수 있습니다.

**Q. 대량 발송 시 속도 제한이 있나요?**
A. Sendgo API 플랜에 따라 TPS(초당 전송 건수) 제한이 있습니다. 자세한 내용은 [Sendgo 요금 정책](https://sendgo.io/pricing)을 확인하세요.

**Q. 발송 결과를 콜백(Webhook)으로 받을 수 있나요?**
A. Sendgo는 발송 결과 Webhook을 지원합니다. Sendgo 콘솔에서 Webhook URL을 등록하세요.

---

## 관련 패키지

Sendgo SDK는 다양한 언어와 프레임워크를 지원합니다:

| 언어/프레임워크 | 패키지 | GitHub |
|----------------|--------|--------|
| **Java / Spring Boot** | `io.sendgo:sendgo-spring-boot-starter` | 현재 리포지토리 |
| Node.js / TypeScript | `@sendgo/node` | [sendgo-node](https://github.com/send-go/sendgo-node) |
| Python / Django / FastAPI | `sendgo-python` | [sendgo-python](https://github.com/send-go/sendgo-python) |
| PHP / Laravel | `techigh/sendgo` | [sendgo-php](https://github.com/send-go/sendgo-php) |
| Go | `github.com/send-go/sendgo-go` | [sendgo-go](https://github.com/send-go/sendgo-go) |
| Ruby on Rails | `sendgo` | [sendgo-ruby](https://github.com/send-go/sendgo-ruby) |
| Flutter / Dart | `sendgo_flutter` | [sendgo-flutter](https://github.com/send-go/sendgo-flutter) |
| React / Next.js | `@sendgo/react` | [sendgo-react](https://github.com/send-go/sendgo-react) |
| Vue.js / Nuxt | `@sendgo/vue` | [sendgo-vue](https://github.com/send-go/sendgo-vue) |
| C# / .NET | `Sendgo.SDK` | [sendgo-dotnet](https://github.com/send-go/sendgo-dotnet) |

---

## 기여하기

버그 리포트, 기능 제안, PR은 언제나 환영합니다.

1. 이슈 등록: [GitHub Issues](https://github.com/send-go/sendgo-spring-boot-starter/issues)
2. PR 제출 전 `main` 브랜치 기준으로 작업해주세요.

---

## 라이선스

MIT License © 2026 [Sendgo](https://sendgo.io)

---

*키워드: 카카오 알림톡 Spring Boot, 카카오 친구톡 Java, SMS 발송 Spring Boot, 알림톡 SDK, 카카오 API 연동 스프링부트, 문자 발송 Java, 카카오 비즈니스 API Spring, Sendgo 스프링부트 스타터*
