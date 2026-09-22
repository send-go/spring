package io.sendgo.spring;

import io.sendgo.AccountClient;
import io.sendgo.SendgoClient;
import io.sendgo.spring.autoconfigure.SendgoAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import static org.assertj.core.api.Assertions.assertThat;

class AccountConfigurationTest {
    @Test
    void accountTokenDoesNotRequireSendingKeys() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SendgoAutoConfiguration.class))
            .withPropertyValues("sendgo.agent-token=test-agent")
            .run(context -> {
                assertThat(context).hasSingleBean(AccountClient.class);
                assertThat(context).doesNotHaveBean(SendgoClient.class);
            });
    }

    @Test
    void noCredentialsDoesNotCreateClients() {
        new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SendgoAutoConfiguration.class))
            .run(context -> {
                assertThat(context).doesNotHaveBean(AccountClient.class);
                assertThat(context).doesNotHaveBean(SendgoClient.class);
            });
    }
}
