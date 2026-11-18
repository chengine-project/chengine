package io.chengine.koog

import example.koog.KoogTelegramDemo
import example.koog.KoogTelegramHandler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.boot.SpringApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.BotSession
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer

class FakePollingApplication : TelegramBotsLongPollingApplication() {
    var registrations = 0
    override fun registerBot(token: String, consumer: LongPollingUpdateConsumer): BotSession? {
        registrations++
        return null
    }
}

@Configuration(proxyBeanMethods = false)
class FakePollingConfig {
    @Bean fun pollingApplication() = FakePollingApplication()
}

class KoogDemoStartupTest {
    @Test fun `demo starts and registers a bot without contacting Telegram`() {
        val application = SpringApplication(KoogTelegramDemo::class.java, FakePollingConfig::class.java)
        application.setDefaultProperties(mapOf(
            "chengine.telegram.token" to "test-token",
            "chengine.telegram.username" to "test_bot",
            "spring.main.web-application-type" to "none",
        ))
        application.run().use { context ->
            assertNotNull(context.getBean(KoogTelegramHandler::class.java))
            assertEquals(1, context.getBean(FakePollingApplication::class.java).registrations)
        }
    }
}
