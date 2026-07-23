package io.sendgo.starter.annotation;

import io.sendgo.starter.autoconfigure.SendgoAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Sendgo SDK를 활성화합니다.
 *
 * <pre>
 * {@literal @}SpringBootApplication
 * {@literal @}EnableSendgo
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * </pre>
 *
 * spring.factories / AutoConfiguration.imports 방식으로도 자동 등록됩니다.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(SendgoAutoConfiguration.class)
public @interface EnableSendgo {
}
