package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.session.Conversations
import io.chengine.session.InMemorySessionStore
import io.chengine.session.Session
import io.chengine.session.SessionKey
import io.chengine.session.SessionStore
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.junit.jupiter.api.Assertions.assertEquals
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.generics.LongPollingBot

@Handler
class StartupHandler {
    @HandleCommand("/start") fun start(request: BotRequestContext): String {
        Conversations.start(request, "name")
        return "started"
    }
}

class TestSessionStore : SessionStore {
    private val delegate = InMemorySessionStore()
    var lastSaved: SessionKey? = null
    override fun load(key: SessionKey): Session? = delegate.load(key)
    override fun save(key: SessionKey, session: Session) { lastSaved = key; delegate.save(key, session) }
    override fun delete(key: SessionKey) = delegate.delete(key)
}

@Configuration
@EnableAutoConfiguration
open class TestApplication {
    @Bean open fun testHandler() = StartupHandler()
    @Bean open fun testSessionStore() = TestSessionStore()
    @Bean open fun telegramBotsApi(): TelegramBotsApi = mock(TelegramBotsApi::class.java)
}

class TelegramStarterIntegrationTest {
    @Test fun `starter initializes and registers bot`() {
        val application = SpringApplication(TestApplication::class.java)
        application.setDefaultProperties(mapOf(
            "chengine.telegram.token" to "test-token",
            "chengine.telegram.username" to "test_bot",
            "spring.main.web-application-type" to "none"
        ))
        val context = application.run()
        try {
            val chengine = context.getBean(Chengine::class.java)
            assertThrows(IllegalArgumentException::class.java) { chengine.register(StartupHandler()) }
            val bot = context.getBean(ChengineTelegramLongPoolingBot::class.java)
            assertNotNull(bot)
            bot.process(Update().apply { message = Message().apply {
                text = "/start"
                chat = Chat().apply { id = 10L }
                from = User().apply { id = 1L }
            } })
            assertEquals("test_bot", context.getBean(TestSessionStore::class.java).lastSaved?.botId)
            verify(context.getBean(TelegramBotsApi::class.java))
                .registerBot(context.getBean(LongPollingBot::class.java))
        } finally {
            context.close()
        }
    }
}
