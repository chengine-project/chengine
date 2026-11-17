package io.chengine

import io.chengine.command.TelegramCommandRequestExtractor
import io.chengine.connector.DefaultBotRequestContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramCommandRequestExtractorTest {
    private fun extract(text: String, username: String?): String? {
        val update = Update().apply { message = Message().apply { this.text = text; chat = Chat().apply { id = 1L } } }
        val request = DefaultBotRequestContext().apply { contextMap[Update::class] = update }
        return TelegramCommandRequestExtractor { username }.extractFrom(request)?.path
    }
    @Test fun `only commands addressed to this bot are accepted`() {
        assertEquals("/hello", extract("/hello@MyBot payload", "mybot"))
        assertNull(extract("/hello@OtherBot", "mybot"))
        assertNull(extract("/hello@MyBot", null))
    }
    @Test fun `payload is separated on whitespace`() {
        assertEquals("/hello", extract("/hello\tpayload", "mybot"))
        assertEquals("/hello", extract("/hello\npayload", "mybot"))
    }
}
