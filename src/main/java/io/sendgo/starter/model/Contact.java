package io.sendgo.starter.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 수신자 정보.
 *
 * <pre>
 * Contact contact = Contact.builder()
 *     .contact("01012345678")
 *     .name("홍길동")
 *     .var1("주문번호-001")
 *     .build();
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {

    /** 수신자 전화번호 (필수) */
    private String contact;

    /** 수신자 이름 (선택) */
    private String name;

    /** 템플릿 변수 1 */
    private String var1;
    /** 템플릿 변수 2 */
    private String var2;
    /** 템플릿 변수 3 */
    private String var3;
    /** 템플릿 변수 4 */
    private String var4;
    /** 템플릿 변수 5 */
    private String var5;
    /** 템플릿 변수 6 */
    private String var6;
    /** 템플릿 변수 7 */
    private String var7;
    /** 템플릿 변수 8 */
    private String var8;

    private Contact() {}

    public static Builder builder() { return new Builder(); }

    public String getContact() { return contact; }
    public String getName() { return name; }
    public String getVar1() { return var1; }
    public String getVar2() { return var2; }
    public String getVar3() { return var3; }
    public String getVar4() { return var4; }
    public String getVar5() { return var5; }
    public String getVar6() { return var6; }
    public String getVar7() { return var7; }
    public String getVar8() { return var8; }

    public static class Builder {
        private final Contact c = new Contact();

        public Builder contact(String contact) { c.contact = contact; return this; }
        public Builder name(String name) { c.name = name; return this; }
        public Builder var1(String v) { c.var1 = v; return this; }
        public Builder var2(String v) { c.var2 = v; return this; }
        public Builder var3(String v) { c.var3 = v; return this; }
        public Builder var4(String v) { c.var4 = v; return this; }
        public Builder var5(String v) { c.var5 = v; return this; }
        public Builder var6(String v) { c.var6 = v; return this; }
        public Builder var7(String v) { c.var7 = v; return this; }
        public Builder var8(String v) { c.var8 = v; return this; }

        public Contact build() {
            if (c.contact == null || c.contact.isBlank()) {
                throw new IllegalArgumentException("contact(전화번호)는 필수입니다.");
            }
            return c;
        }
    }
}
