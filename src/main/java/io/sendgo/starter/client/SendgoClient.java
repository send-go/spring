package io.sendgo.starter.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sendgo.starter.config.SendgoProperties;
import io.sendgo.starter.exception.SendgoException;
import io.sendgo.starter.model.AlimtalkRequest;
import io.sendgo.starter.model.FriendtalkRequest;
import io.sendgo.starter.model.SmsRequest;
import org.springframework.http.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Sendgo API 클라이언트.
 * 토큰 자동 발급/갱신, 알림톡/친구톡/SMS 전송 기능을 제공합니다.
 *
 * <pre>
 * // Spring Bean으로 주입받아 사용
 * {@literal @}Autowired
 * private SendgoClient sendgo;
 *
 * sendgo.sendAlimtalk(AlimtalkRequest.builder()
 *     .templateCode("ORDER_CONFIRM_001")
 *     .contact(Contact.builder().contact("01012345678").var1("ORD-001").build())
 *     .build());
 * </pre>
 */
public class SendgoClient {

    private static final Set<String> NO_REFRESH_CODES = Set.of(
            "INVALID_AUTH_HEADER", "INVALID_BASIC_AUTH", "INVALID_BASIC_AUTH_PAYLOAD",
            "INVALID_ACCESS_KEY", "INVALID_SECRET_KEY", "ACCESS_KEY_NOT_APPROVED",
            "TEAM_REQUIRED_FOR_KAKAO", "IP_NOT_ALLOWED",
            "INVALID_SENDER_KEY", "INVALID_KAKAO_SENDER_KEY"
    );

    private final SendgoProperties properties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // 토큰 인메모리 캐시
    private volatile String cachedToken;
    private volatile long tokenExpiresAt = 0L;

    public SendgoClient(SendgoProperties properties) {
        this.properties = properties;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
        // 초기 토큰 발급
        getToken();
    }

    // ----------------------------------------------------------------
    // Public API
    // ----------------------------------------------------------------

    /** 카카오 알림톡 전송 */
    public Map<String, Object> sendAlimtalk(AlimtalkRequest request) {
        Map<String, Object> body = toMap(request);
        body.put("kakaoSenderKey", properties.getKakaoSenderKey());
        body.put("senderKey", properties.getSmsSenderKey());
        return performSend(buildUrl("notices"), body);
    }

    /** 카카오 친구톡 전송 */
    public Map<String, Object> sendFriendtalk(FriendtalkRequest request) {
        Map<String, Object> body = toMap(request);
        body.put("kakaoSenderKey", properties.getKakaoSenderKey());
        body.put("senderKey", properties.getSmsSenderKey());
        return performSend(buildUrl("friends"), body);
    }

    /** SMS 전송 (SmsRequest.sms()로 생성) */
    public Map<String, Object> sendSms(SmsRequest request) {
        return sendMessage(request);
    }

    /** LMS 전송 (SmsRequest.lms()로 생성) */
    public Map<String, Object> sendLms(SmsRequest request) {
        return sendMessage(request);
    }

    /** MMS 전송 (SmsRequest.mms()로 생성) */
    public Map<String, Object> sendMms(SmsRequest request) {
        return sendMessage(request);
    }

    // ----------------------------------------------------------------
    // Internal
    // ----------------------------------------------------------------

    private Map<String, Object> sendMessage(SmsRequest request) {
        Map<String, Object> body = toMap(request);
        body.put("senderKey", properties.getSmsSenderKey());
        return performSend(buildUrl("messages"), body);
    }

    private Map<String, Object> performSend(String url, Map<String, Object> body) {
        return doRequest(url, body, false);
    }

    private Map<String, Object> doRequest(String url, Map<String, Object> body, boolean isRetry) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", makeBearerAuth());

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> result = (Map<String, Object>) response.getBody();
            return result != null ? result : Map.of();
        } catch (HttpStatusCodeException e) {
            int status = e.getStatusCode().value();
            String responseBody = e.getResponseBodyAsString();
            String endpoint = url.substring(url.lastIndexOf('/') + 1);

            if (!isRetry && shouldRefreshToken(status, responseBody)) {
                invalidateToken();
                return doRequest(url, body, true);
            }

            throw SendgoException.fromResponse(status, responseBody, endpoint, properties.getApiVersion());
        }
    }

    // ----------------------------------------------------------------
    // Token Management
    // ----------------------------------------------------------------

    private synchronized String getToken() {
        long now = System.currentTimeMillis();
        if (cachedToken != null && now < tokenExpiresAt) {
            return cachedToken;
        }
        return fetchNewToken();
    }

    private String fetchNewToken() {
        String tokenUrl = properties.getUrl() + "/api/" + properties.getApiVersion() + "/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", makeBasicAuth());

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);
            Map<?, ?> responseBody = response.getBody();

            if (responseBody == null) {
                throw new SendgoException("SendGo 토큰 발급 실패: 빈 응답");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
            if (data == null || data.get("token") == null) {
                throw new SendgoException("SendGo 토큰 발급 실패: token 필드 없음");
            }

            String token = (String) data.get("token");
            this.cachedToken = token;
            this.tokenExpiresAt = System.currentTimeMillis() + (50L * 60 * 1000);
            return token;

        } catch (HttpStatusCodeException e) {
            throw SendgoException.fromResponse(
                    e.getStatusCode().value(),
                    e.getResponseBodyAsString(),
                    "token",
                    properties.getApiVersion()
            );
        }
    }

    private synchronized void invalidateToken() {
        this.cachedToken = null;
        this.tokenExpiresAt = 0L;
        getToken();
    }

    private boolean shouldRefreshToken(int status, String responseBody) {
        if (status != 401 && status != 403) return false;

        if ("v2".equals(properties.getApiVersion())) {
            try {
                Map<String, Object> body = objectMapper.readValue(responseBody, new TypeReference<>() {});
                String code = (String) body.get("code");
                return code == null || !NO_REFRESH_CODES.contains(code);
            } catch (Exception ignored) {
                return true;
            }
        }
        return true; // v1: 401/403이면 항상 재발급
    }

    // ----------------------------------------------------------------
    // Authorization Helpers
    // ----------------------------------------------------------------

    private String makeBasicAuth() {
        String credentials = properties.getAccessKey() + ":" + properties.getSecretKey();
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());
    }

    private String makeBearerAuth() {
        String token = getToken();
        if ("v2".equals(properties.getApiVersion())) {
            return "Bearer " + token;
        }
        return "Bearer " + Base64.getEncoder().encodeToString(token.getBytes());
    }

    // ----------------------------------------------------------------
    // URL Builder
    // ----------------------------------------------------------------

    private String buildUrl(String resource) {
        return properties.getUrl() + "/api/" + properties.getApiVersion() + "/" + resource + "/send";
    }

    // ----------------------------------------------------------------
    // Serialization
    // ----------------------------------------------------------------

    private Map<String, Object> toMap(Object obj) {
        return objectMapper.convertValue(obj, new TypeReference<>() {});
    }
}
