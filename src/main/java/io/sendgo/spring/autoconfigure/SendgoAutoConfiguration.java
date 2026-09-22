package io.sendgo.spring.autoconfigure;

import io.sendgo.SendgoClient;
import io.sendgo.AccountClient;
import io.sendgo.SendgoConfig;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Sendgo Spring Boot 자동 구성.
 * {@code sendgo.access-key} 프로퍼티가 설정되면 {@link SendgoClient} 빈이 자동 등록됩니다.
 */
@AutoConfiguration
@EnableConfigurationProperties(SendgoProperties.class)
public class SendgoAutoConfiguration {

    /** 에이전트 토큰만으로 계정 API를 구성합니다. */
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sendgo", name = "agent-token")
    public AccountClient accountClient(SendgoProperties props) {
        return new AccountClient(props.getAgentToken(), props.getUrl());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "sendgo", name = "access-key")
    public SendgoClient sendgoClient(SendgoProperties props) {
        return new SendgoClient(SendgoConfig.builder()
                .baseUrl(props.getUrl())
                .accessKey(props.getAccessKey())
                .secretKey(props.getSecretKey())
                .kakaoSenderKey(props.getKakaoSenderKey())
                .smsSenderKey(props.getSmsSenderKey())
                .apiVersion(props.getApiVersion())
                .build());
    }
}
