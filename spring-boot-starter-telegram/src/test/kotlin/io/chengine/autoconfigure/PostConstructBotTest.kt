package io.chengine.autoconfigure

import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.dsl.sendTelegramMessage
import io.chengine.execute
import jakarta.annotation.PostConstruct
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.verify
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient

class EarlySender(private val bot: ChengineTelegramLongPoolingBot) {
    @PostConstruct fun send() { bot.execute { sendTelegramMessage { chatId = "123"; text = "startup" } } }
}

@Configuration(proxyBeanMethods = false)
class EarlySendConfiguration {
    @Bean fun earlySender(bot: ChengineTelegramLongPoolingBot) = EarlySender(bot)
}

class PostConstructBotTest {
    @Test fun `bot can send during post construct`() {
        val application = SpringApplication(TestApplication::class.java, EarlySendConfiguration::class.java)
        application.setDefaultProperties(mapOf("chengine.telegram.token" to "test-token", "chengine.telegram.username" to "test_bot", "spring.main.web-application-type" to "none"))
        application.run().use { context -> verify(context.getBean(TelegramClient::class.java)).execute(any(SendMessage::class.java)) }
    }
}
