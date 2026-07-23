package io.sendgo.starter.autoconfigure;

import io.sendgo.starter.client.SendgoClient;
import io.sendgo.starter.config.SendgoProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Sendgo Spring Boot Auto-Configuration.
 *
 * application.yml에 sendgo.access-key가 설정된 경우 자동으로 {@link SendgoClient} Bean을 등록합니다.
 */
@AutoConfiguration
@EnableConfigurationProperties(SendgoProperties.class)
@ConditionalOnProperty(prefix = "sendgo", name = "access-key")
public class SendgoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public SendgoClient sendgoClient(SendgoProperties properties) {
        return new SendgoClient(properties);
    }
}
