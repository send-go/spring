package io.sendgo.spring;

import io.sendgo.SendgoClient;
import io.sendgo.SendgoConfig;
import io.sendgo.model.NoticeTemplateRequest;
import io.sendgo.model.ShortUrlRequest;
import io.sendgo.model.WebhookSubscriptionRequest;

/**
 * 코어(sendgo-java)의 공개 표면이 스타터에서 그대로 보이는지 컴파일로 확인한다.
 *
 * 스타터는 코어를 재수출만 하므로, 코어에 서비스가 늘었는데 스타터의 의존성
 * 버전을 올리지 않으면 여기서 컴파일이 깨진다 — 그게 이 테스트의 목적이다.
 */
class CoreSurfaceTest {
    void shortUrlIsReachable() {
        SendgoClient sendgo = client();

        sendgo.shortUrl().create(ShortUrlRequest.builder()
                .targetUrl("https://example.com")
                .build());
    }

    /** 1.3.0 관리 API — 채널 등록·템플릿 검수·발신번호 심사 접수. */
    void managementApiIsReachable() {
        SendgoClient sendgo = client();

        sendgo.kakaoSenders().requestToken("@my-channel", "01012345678");
        sendgo.kakaoSenders().sync();

        sendgo.noticeTemplates().create(NoticeTemplateRequest.builder()
                .kakaoSenderKey("sender-key")
                .templateName("주문 접수 안내")
                .templateContent("#{name}님, 주문이 접수되었습니다.")
                .categoryCode("001001")
                .messagePurpose("order_delivery")
                .legalBasis("transaction")
                .benefitOrigin("none")
                .expiryType("none")
                .build());

        sendgo.noticeTemplates().requestInspection("TPL-0001");
        sendgo.noticeTemplates().sync("TPL-0001");

        sendgo.brandTemplates().list();
        sendgo.senderRegistration().numberTypes();
        sendgo.messageTemplates().list();

        // 리셀러가 콘솔 없이 운영하려면 이 셋이 더 필요하다.
        sendgo.kakaoImages().types();
        sendgo.rejectedNumbers().list("2026-09-01", null, 500);
        sendgo.webhook().subscribe(WebhookSubscriptionRequest.builder()
                .url("https://reseller.example.com/hooks/sendgo")
                .event(WebhookSubscriptionRequest.EVENT_SENDER_STATUS)
                .build());
    }

    private SendgoClient client() {
        return new SendgoClient(SendgoConfig.builder().accessKey("a").secretKey("b").build());
    }
}
