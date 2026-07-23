package io.sendgo.starter.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * 카카오 알림톡 전송 요청.
 *
 * <pre>
 * AlimtalkRequest request = AlimtalkRequest.builder()
 *     .templateCode("ORDER_CONFIRM_001")
 *     .contact(Contact.builder()
 *         .contact("01012345678")
 *         .name("홍길동")
 *         .var1("ORD-001")
 *         .build())
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AlimtalkRequest {

    /** 발송 예약 시각 (null = 즉시 발송) */
    private String at;

    /** 발송 유형 (DIRECTLY | SCHEDULED, 기본값: DIRECTLY) */
    private String scheduleType = "DIRECTLY";

    /** 승인된 알림톡 템플릿 코드 (필수) */
    private String templateCode;

    /** 알림톡 실패 시 SMS 대체 발송 여부 (Y | N, 기본값: N) */
    private String replaceSms = "N";

    /** 대체 SMS 제목 (replaceSms=Y 일 때 필수) */
    private String smsSubject;

    /** 대체 SMS 내용 (replaceSms=Y 일 때 필수) */
    private String smsContent;

    /** 수신자 목록 */
    private List<Contact> contacts = new ArrayList<>();

    private AlimtalkRequest() {}

    public static Builder builder() { return new Builder(); }

    public String getAt() { return at; }
    public String getScheduleType() { return scheduleType; }
    public String getTemplateCode() { return templateCode; }
    public String getReplaceSms() { return replaceSms; }
    public String getSmsSubject() { return smsSubject; }
    public String getSmsContent() { return smsContent; }
    public List<Contact> getContacts() { return contacts; }

    public static class Builder {
        private final AlimtalkRequest r = new AlimtalkRequest();

        public Builder at(String at) { r.at = at; return this; }
        public Builder scheduleType(String scheduleType) { r.scheduleType = scheduleType; return this; }
        public Builder templateCode(String templateCode) { r.templateCode = templateCode; return this; }

        /** 알림톡 실패 시 SMS 대체 발송 활성화 */
        public Builder replaceSms(String smsSubject, String smsContent) {
            r.replaceSms = "Y";
            r.smsSubject = smsSubject;
            r.smsContent = smsContent;
            return this;
        }

        public Builder contact(Contact contact) {
            r.contacts.add(contact);
            return this;
        }

        public Builder contacts(List<Contact> contacts) {
            r.contacts = new ArrayList<>(contacts);
            return this;
        }

        public AlimtalkRequest build() {
            if (r.templateCode == null || r.templateCode.isBlank()) {
                throw new IllegalArgumentException("templateCode는 필수입니다.");
            }
            if (r.contacts.isEmpty()) {
                throw new IllegalArgumentException("수신자(contacts)는 최소 1명 이상이어야 합니다.");
            }
            return r;
        }
    }
}
