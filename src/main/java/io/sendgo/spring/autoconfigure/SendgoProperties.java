package io.sendgo.spring.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sendgo 설정 프로퍼티.
 *
 * <pre>
 * # application.yml
 * sendgo:
 *   access-key: ${SENDGO_ACCESS_KEY}
 *   secret-key: ${SENDGO_SECRET_KEY}
 *   kakao-sender-key: ${SENDGO_KAKAO_SENDER_KEY}
 *   sms-sender-key: ${SENDGO_SMS_SENDER_KEY}
 *   api-version: v2
 * </pre>
 */
@ConfigurationProperties(prefix = "sendgo")
public class SendgoProperties {

    private String url           = "https://api.sendgo.io";
    private String accessKey;
    private String secretKey;
    private String kakaoSenderKey;
    private String smsSenderKey;
    private String apiVersion    = "v2";

    public String getUrl()            { return url; }
    public String getAccessKey()      { return accessKey; }
    public String getSecretKey()      { return secretKey; }
    public String getKakaoSenderKey() { return kakaoSenderKey; }
    public String getSmsSenderKey()   { return smsSenderKey; }
    public String getApiVersion()     { return apiVersion; }

    public void setUrl(String v)            { url = v; }
    public void setAccessKey(String v)      { accessKey = v; }
    public void setSecretKey(String v)      { secretKey = v; }
    public void setKakaoSenderKey(String v) { kakaoSenderKey = v; }
    public void setSmsSenderKey(String v)   { smsSenderKey = v; }
    public void setApiVersion(String v)     { apiVersion = v; }
}
