package io.sendgo.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Sendgo 설정 Properties.
 *
 * application.yml 예시:
 * <pre>
 * sendgo:
 *   access-key: your_access_key
 *   secret-key: your_secret_key
 *   sms-sender-key: your_sms_sender_key
 *   kakao-sender-key: your_kakao_sender_key
 *   api-version: v2
 * </pre>
 */
@ConfigurationProperties(prefix = "sendgo")
public class SendgoProperties {

    /** Sendgo API 기본 URL (기본값: https://api.sendgo.io) */
    private String url = "https://api.sendgo.io";

    /** Sendgo Access Key (필수) */
    private String accessKey;

    /** Sendgo Secret Key (필수) */
    private String secretKey;

    /** SMS 발신자 키 */
    private String smsSenderKey;

    /** 카카오 발신프로필 키 */
    private String kakaoSenderKey;

    /** API 버전 (v1 | v2, 기본값: v1) */
    private String apiVersion = "v1";

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getSecretKey() { return secretKey; }
    public void setSecretKey(String secretKey) { this.secretKey = secretKey; }

    public String getSmsSenderKey() { return smsSenderKey; }
    public void setSmsSenderKey(String smsSenderKey) { this.smsSenderKey = smsSenderKey; }

    public String getKakaoSenderKey() { return kakaoSenderKey; }
    public void setKakaoSenderKey(String kakaoSenderKey) { this.kakaoSenderKey = kakaoSenderKey; }

    public String getApiVersion() { return apiVersion; }
    public void setApiVersion(String apiVersion) { this.apiVersion = apiVersion; }
}
