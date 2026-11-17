package io.chengine.autoconfigure

import io.chengine.command.HandleCommand
import io.chengine.handler.Handler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient

@Handler
class WebhookTestHandler {
    @HandleCommand("/webhook") fun reply(): String = "Webhook works"
}

@Configuration(proxyBeanMethods = false)
@EnableAutoConfiguration
class WebhookTestApplication {
    @Bean fun client(): TelegramClient = mock(TelegramClient::class.java)
    @Bean fun handler() = WebhookTestHandler()
}

@SpringBootTest(classes = [WebhookTestApplication::class], properties = [
    "chengine.telegram.token=test-token", "chengine.telegram.username=test_bot",
    "chengine.telegram.mode=webhook", "chengine.telegram.webhook.secret=test_secret",
    "chengine.telegram.webhook.path=/test-webhook"
])
@AutoConfigureMockMvc
class WebhookIntegrationTest {
    @Autowired lateinit var mvc: MockMvc
    @Autowired lateinit var client: TelegramClient
    @Autowired lateinit var context: org.springframework.context.ApplicationContext

    private val update = """{"update_id":1,"message":{"message_id":1,"date":1,"chat":{"id":123,"type":"private"},"from":{"id":9,"is_bot":false,"first_name":"Test"},"text":"/webhook"}}"""

    @Test fun `accepts verified update and does not start polling`() {
        mvc.perform(post("/test-webhook").header("X-Telegram-Bot-Api-Secret-Token", "test_secret")
            .contentType(MediaType.APPLICATION_JSON).content(update)).andExpect(status().isOk)
        val message = ArgumentCaptor.forClass(SendMessage::class.java)
        verify(client).execute(message.capture())
        assertEquals("123", message.value.chatId)
        assertEquals("Webhook works", message.value.text)
        check(context.getBeansOfType(org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication::class.java).isEmpty())
    }

    @Test fun `rejects missing or incorrect secret`() {
        mvc.perform(post("/test-webhook").contentType(MediaType.APPLICATION_JSON).content(update)).andExpect(status().isForbidden)
        mvc.perform(post("/test-webhook").header("X-Telegram-Bot-Api-Secret-Token", "wrong")
            .contentType(MediaType.APPLICATION_JSON).content(update)).andExpect(status().isForbidden)
    }

    @Test fun `requires a valid webhook secret`() {
        val bot = mock(io.chengine.ChengineTelegramLongPoolingBot::class.java)
        assertThrows(IllegalArgumentException::class.java) { TelegramWebhookController(bot, "") }
        assertThrows(IllegalArgumentException::class.java) { TelegramWebhookController(bot, "bad secret") }
    }
}
