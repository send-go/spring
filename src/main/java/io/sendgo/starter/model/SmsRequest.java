package io.sendgo.starter.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * SMS / LMS / MMS 전송 요청.
 *
 * <pre>
 * // SMS
 * SmsRequest request = SmsRequest.sms()
 *     .content("안녕하세요.")
 *     .contact(Contact.builder().contact("01012345678").build())
 *     .build();
 *
 * // LMS (장문)
 * SmsRequest request = SmsRequest.lms()
 *     .subject("[공지사항]")
 *     .content("긴 내용...")
 *     .contact(Contact.builder().contact("01012345678").build())
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmsRequest {

    /** 캠페인 유형 (MESSAGE | ADVERTISE | ELECTION, 기본값: MESSAGE) */
    private String campaignType = "MESSAGE";

    /** 메시지 유형 (SMS | LMS | MMS) */
    private String messageType;

    /** 발송 유형 (DIRECTLY | SCHEDULED, 기본값: DIRECTLY) */
    private String scheduleType = "DIRECTLY";

    /** 발송 예약 시각 (null = 즉시 발송) */
    private String at;

    /** 메시지 제목 (LMS/MMS에서 사용) */
    private String subject;

    /** 메시지 본문 (필수) */
    private String content;

    /** 첨부 파일 목록 (MMS 이미지 등) */
    private List<Object> files = new ArrayList<>();

    /** 수신자 목록 */
    private List<Contact> contacts = new ArrayList<>();

    private SmsRequest() {}

    /** SMS 전송용 Builder */
    public static Builder sms() { return new Builder("SMS"); }

    /** LMS (장문 SMS) 전송용 Builder */
    public static Builder lms() { return new Builder("LMS"); }

    /** MMS (멀티미디어 SMS) 전송용 Builder */
    public static Builder mms() { return new Builder("MMS"); }

    public String getCampaignType() { return campaignType; }
    public String getMessageType() { return messageType; }
    public String getScheduleType() { return scheduleType; }
    public String getAt() { return at; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public List<Object> getFiles() { return files; }
    public List<Contact> getContacts() { return contacts; }

    public static class Builder {
        private final SmsRequest r = new SmsRequest();

        private Builder(String messageType) { r.messageType = messageType; }

        public Builder campaignType(String campaignType) { r.campaignType = campaignType; return this; }
        public Builder scheduleType(String scheduleType) { r.scheduleType = scheduleType; return this; }
        public Builder at(String at) { r.at = at; return this; }
        public Builder subject(String subject) { r.subject = subject; return this; }
        public Builder content(String content) { r.content = content; return this; }
        public Builder files(List<Object> files) { r.files = files; return this; }
        public Builder contact(Contact contact) { r.contacts.add(contact); return this; }
        public Builder contacts(List<Contact> contacts) { r.contacts = new ArrayList<>(contacts); return this; }

        public SmsRequest build() {
            if (r.content == null || r.content.isBlank()) {
                throw new IllegalArgumentException("content(메시지 내용)는 필수입니다.");
            }
            if (r.contacts.isEmpty()) {
                throw new IllegalArgumentException("수신자(contacts)는 최소 1명 이상이어야 합니다.");
            }
            return r;
        }
    }
}
