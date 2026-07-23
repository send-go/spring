package io.sendgo.starter.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

/**
 * 카카오 친구톡 전송 요청.
 *
 * <pre>
 * FriendtalkRequest request = FriendtalkRequest.builder()
 *     .content("안녕하세요! 봄맞이 30% 할인 이벤트입니다.")
 *     .contact(Contact.builder().contact("01012345678").build())
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FriendtalkRequest {

    /** 발송 예약 시각 (null = 즉시 발송) */
    private String at;

    /** 발송 유형 (DIRECTLY | SCHEDULED, 기본값: DIRECTLY) */
    private String scheduleType = "DIRECTLY";

    /**
     * 메시지 유형
     * FT(기본 텍스트) | FI(이미지형) | FW(와이드 이미지) | FL(리스트형)
     * FM(이미지+텍스트) | FC(캐러셀) | FA(아이템 리스트) | FP(프리미엄 동영상)
     */
    private String messageType = "FT";

    /** 메시지 본문 (필수) */
    private String content;

    /** 버튼 목록 */
    private List<Object> buttons = new ArrayList<>();

    /** 이미지 파일 (imageUrl과 둘 중 하나 필요) */
    private Object image;

    /** 이미지 URL (image와 둘 중 하나 필요) */
    private String imageUrl;

    /** 이미지 링크 URL */
    private String imageLink;

    /** 광고성 메시지 여부 (Y | N, 기본값: Y) */
    private String adFlag = "Y";

    /** 와이드 이미지 여부 (Y | N, 기본값: N) */
    private String wide = "N";

    /** 성인 콘텐츠 여부 (Y | N, 기본값: N) */
    private String adult = "N";

    /** 헤더 텍스트 */
    private String header;

    /** 알림톡 실패 시 SMS 대체 발송 여부 (Y | N, 기본값: N) */
    private String replaceSms = "N";

    /** 대체 SMS 제목 */
    private String smsSubject;

    /** 대체 SMS 내용 */
    private String smsContent;

    /** 수신자 목록 */
    private List<Contact> contacts = new ArrayList<>();

    private FriendtalkRequest() {}

    public static Builder builder() { return new Builder(); }

    public String getAt() { return at; }
    public String getScheduleType() { return scheduleType; }
    public String getMessageType() { return messageType; }
    public String getContent() { return content; }
    public List<Object> getButtons() { return buttons; }
    public Object getImage() { return image; }
    public String getImageUrl() { return imageUrl; }
    public String getImageLink() { return imageLink; }
    public String getAdFlag() { return adFlag; }
    public String getWide() { return wide; }
    public String getAdult() { return adult; }
    public String getHeader() { return header; }
    public String getReplaceSms() { return replaceSms; }
    public String getSmsSubject() { return smsSubject; }
    public String getSmsContent() { return smsContent; }
    public List<Contact> getContacts() { return contacts; }

    public static class Builder {
        private final FriendtalkRequest r = new FriendtalkRequest();

        public Builder at(String at) { r.at = at; return this; }
        public Builder scheduleType(String scheduleType) { r.scheduleType = scheduleType; return this; }
        public Builder messageType(String messageType) { r.messageType = messageType; return this; }
        public Builder content(String content) { r.content = content; return this; }
        public Builder buttons(List<Object> buttons) { r.buttons = buttons; return this; }
        public Builder image(Object image) { r.image = image; return this; }
        public Builder imageUrl(String imageUrl) { r.imageUrl = imageUrl; return this; }
        public Builder imageLink(String imageLink) { r.imageLink = imageLink; return this; }
        public Builder adFlag(String adFlag) { r.adFlag = adFlag; return this; }
        public Builder wide(String wide) { r.wide = wide; return this; }
        public Builder adult(String adult) { r.adult = adult; return this; }
        public Builder header(String header) { r.header = header; return this; }

        public Builder replaceSms(String smsSubject, String smsContent) {
            r.replaceSms = "Y";
            r.smsSubject = smsSubject;
            r.smsContent = smsContent;
            return this;
        }

        public Builder contact(Contact contact) { r.contacts.add(contact); return this; }
        public Builder contacts(List<Contact> contacts) { r.contacts = new ArrayList<>(contacts); return this; }

        public FriendtalkRequest build() {
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
