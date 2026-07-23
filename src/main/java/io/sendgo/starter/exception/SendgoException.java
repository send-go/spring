package io.sendgo.starter.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * Sendgo API 호출 실패 시 발생하는 예외.
 */
public class SendgoException extends RuntimeException {

    private final int statusCode;
    private final String errorCode;
    private final String endpoint;
    private final String apiVersion;

    public SendgoException(String message, int statusCode, String errorCode, String endpoint, String apiVersion) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.endpoint = endpoint;
        this.apiVersion = apiVersion;
    }

    public SendgoException(String message) {
        super(message);
        this.statusCode = 0;
        this.errorCode = null;
        this.endpoint = null;
        this.apiVersion = null;
    }

    public static SendgoException fromResponse(int status, String responseBody, String endpoint, String apiVersion) {
        String errorCode = null;
        String errorMessage = "Unknown error";

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> body = mapper.readValue(responseBody, new TypeReference<>() {});
            errorCode = (String) body.get("code");
            if (body.get("message") instanceof String m) {
                errorMessage = m;
            }
        } catch (Exception ignored) {
            errorMessage = responseBody;
        }

        String message = "HTTP " + status;
        if (errorCode != null) message += " [" + errorCode + "]";
        message += " " + errorMessage;

        return new SendgoException(message, status, errorCode, endpoint, apiVersion);
    }

    public int getStatusCode() { return statusCode; }
    public String getErrorCode() { return errorCode; }
    public String getEndpoint() { return endpoint; }
    public String getApiVersion() { return apiVersion; }

    @Override
    public String toString() {
        return "SendgoException{statusCode=" + statusCode
                + ", errorCode='" + errorCode + "'"
                + ", endpoint='" + endpoint + "'"
                + ", apiVersion='" + apiVersion + "'"
                + ", message='" + getMessage() + "'}";
    }
}
