package io.chengine.verification;

import io.chengine.Chengine;
import io.chengine.autoconfigure.ChengineTelegramAutoConfiguration;
import io.chengine.session.postgres.PostgresSessionStore;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;

import java.util.Map;

public class Main {
    static boolean registered;

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    static class Application {
        @Bean
        TelegramBotsLongPollingApplication telegramBotsApplication() {
            return new TelegramBotsLongPollingApplication() {
                @Override
                public BotSession registerBot(String token, LongPollingUpdateConsumer consumer) {
                    registered = "test-token".equals(token);
                    return null;
                }
            };
        }
    }

    public static void main(String[] args) throws Exception {
        for (Class<?> type : new Class<?>[] {Chengine.class, ChengineTelegramAutoConfiguration.class,
                PostgresSessionStore.class, Update.class}) {
            Class.forName(type.getName());
        }
        SpringApplication application = new SpringApplication(Application.class);
        application.setDefaultProperties(Map.of(
                "chengine.telegram.token", "test-token",
                "chengine.telegram.username", "test_bot",
                "spring.main.web-application-type", "none"));
        try (var context = application.run()) {
            if (!registered || context.getBean(Chengine.class) == null) {
                throw new IllegalStateException("Starter did not initialize the bot");
            }
        }
        System.out.println("Published Chengine modules loaded successfully");
    }
}
