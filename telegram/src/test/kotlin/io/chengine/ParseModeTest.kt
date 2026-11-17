package io.chengine

import io.chengine.dsl.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ParseModeTest {
    @Test fun `builders use Telegram parse mode values`() {
        for (mode in ParseMode.values()) {
            val value = mode.apiValue
            assertEquals(value, sendTelegramMessage { text = "*text*"; parseMode = mode }.parseMode)
            assertEquals(value, sendTelegramPhoto { photo { name = "file-id" }; parseMode = mode }.parseMode)
            assertEquals(value, editTelegramMessageText { messageId = 1; text = "text"; parseMode = mode }.parseMode)
            assertEquals(value, sendTelegramPoll { question = "Q?"; options = listOf("A", "B"); type = TelegramPollType.QUIZ; correctOptionIds = listOf(0); explanationParseMode = mode }.explanationParseMode)
        }
        assertEquals("MarkdownV2", ParseMode.MARKDOWN_V2.apiValue)
    }
}
