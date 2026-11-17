package io.chengine

import io.chengine.command.HandleCommand
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.*
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendVenue
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageLiveLocation
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import java.io.ByteArrayInputStream

@Handler class RemainingDslHandler {
    @HandleCommand("/note") fun note() = sendTelegramVideoNote { videoNote { name = "file-id" } }
    @HandleCommand("/venue") fun venue() = sendTelegramVenue { latitude = 55.75; longitude = 37.61; title = "Place"; address = "Address" }
    @HandleCommand("/live") fun live() = editTelegramMessageLiveLocation { messageId = 42; latitude = 55.75; longitude = 37.61 }
    @HandleCommand("/inline") fun inline() = editTelegramMessageLiveLocation { inlineMessageId = "inline"; latitude = 55.75; longitude = 37.61 }
}
private class RemainingDslBot : ChengineTelegramLongPoolingBot() {
    var result: Any? = null
    override fun executeResponse(response: BotResponseContext) { result = response.currentResponseObject() }
}
class RemainingDslTest {
    private fun bot() = RemainingDslBot().also { Chengine(ChengineConfig(listOf(RemainingDslHandler()), listOf(it))) }
    private fun update(text: String) = Update().apply { message = Message().apply { this.text = text; chat = Chat(123, "private") } }
    @Test fun `video venue and live location get correct chat or inline target`() {
        val bot = bot()
        bot.process(update("/note")); assertEquals("123", (bot.result as SendVideoNote).chatId)
        bot.process(update("/venue")); assertEquals("123", (bot.result as SendVenue).chatId)
        bot.process(update("/live")); assertEquals("123", (bot.result as EditMessageLiveLocation).chatId)
        bot.process(update("/inline")); assertNull((bot.result as EditMessageLiveLocation).chatId)
        assertNotNull(sendTelegramVideoNote { videoNote { name = "note.mp4"; mediaStream = ByteArrayInputStream(byteArrayOf(1)) } }.videoNote.newMediaStream)
    }
    @Test fun `validates missing files venue fields and location targets`() {
        assertThrows(IllegalArgumentException::class.java) { sendTelegramVideoNote {} }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramVenue { latitude = 91.0; longitude = 2.0; title = "x"; address = "y" } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramVenue { latitude = 1.0; longitude = 2.0; title = "x"; address = "y"; replyToMessageId = "x" } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageLiveLocation { latitude = 1.0; longitude = 2.0 } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageLiveLocation { messageId = 1; inlineMessageId = "x"; latitude = 1.0; longitude = 2.0 } }
    }
    @Test fun `reply keyboard creates rows removes keyboard and rejects conflicting markup`() {
        val message = sendTelegramMessage { text = "Menu"; replyKeyboard {
            row { button { text = "Yes" }; button { text = "No" } }
            row { button { text = "Location"; requestLocation = true } }
        } }
        val markup = message.replyMarkup as ReplyKeyboardMarkup
        assertEquals(listOf("Yes", "No"), markup.keyboard[0].map { it.text })
        assertEquals(true, markup.keyboard[1][0].requestLocation)
        assertTrue((sendTelegramMessage { text = "Done"; replyKeyboard { removeKeyboard() } }.replyMarkup as ReplyKeyboardRemove).removeKeyboard)
        assertThrows(IllegalArgumentException::class.java) { sendTelegramMessage { text = "Bad"; replyKeyboard {}; } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramMessage { text = "Bad"; replyKeyboard { removeKeyboard() }; inlineKeyboard {} } }
    }
    @Test fun `partial keyboard edits do not mutate original`() {
        val original = sendTelegramMessage { text = "Menu"; inlineKeyboard {
            row { button { text = "Old"; callbackData = "old" } }
            row { button { text = "Link"; url = "https://example.com" } }
        } }.replyMarkup as InlineKeyboardMarkup
        val edited = editInlineKeyboardMarkup(original) {
            editButton(0, 0) { text = "New"; url = "https://example.org" }
            deleteRow(1)
        }
        assertEquals(2, original.keyboard.size)
        assertEquals("old", original.keyboard[0][0].callbackData)
        assertEquals(1, edited.keyboard.size)
        assertEquals("New", edited.keyboard[0][0].text)
        assertEquals("https://example.org", edited.keyboard[0][0].url)
        assertNull(edited.keyboard[0][0].callbackData)
        assertTrue(editInlineKeyboardMarkup(original) { deleteKeyboard() }.keyboard.isEmpty())
        assertThrows(IllegalArgumentException::class.java) { editInlineKeyboardMarkup(original) { deleteRow(4) } }
    }
}
