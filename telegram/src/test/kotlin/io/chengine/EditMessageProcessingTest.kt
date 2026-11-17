package io.chengine

import example.editmessage.EditMessageHandler
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.editTelegramMessageReplyMarkup
import io.chengine.dsl.editTelegramMessageText
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message

private class EditCapturingBot : ChengineTelegramLongPoolingBot() {
    var result: Any? = null
    override fun executeResponse(response: BotResponseContext) { result = response.currentResponseObject() }
}
class EditMessageProcessingTest {
    private fun bot() = EditCapturingBot().also { Chengine(ChengineConfig(listOf(EditMessageHandler()), listOf(it))) }
    private fun callback(data: String) = Update().apply {
        callbackQuery = CallbackQuery().apply {
            id = "query"; this.data = data
            message = Message().apply { messageId = 42; chat = Chat(123, "private") }
        }
    }
    @Test fun `callback edits text and then replaces keyboard with correct target`() {
        val bot = bot()
        bot.process(callback("edit_text"))
        val first = bot.result as List<*>
        assertTrue(first[0] is AnswerCallbackQuery)
        val text = first[1] as EditMessageText
        assertEquals("123", text.chatId)
        assertEquals(42, text.messageId)
        assertEquals("Текст изменён", text.text)
        assertEquals("edit_keyboard", text.replyMarkup.keyboard[0][0].callbackData)
        bot.process(callback("edit_keyboard"))
        val edit = (bot.result as List<*>)[1] as EditMessageReplyMarkup
        assertEquals("123", edit.chatId)
        assertEquals(42, edit.messageId)
        assertEquals("https://chengine-project.github.io/chengine/", edit.replyMarkup.keyboard[0][0].url)
    }
    @Test fun `supports inline targets and removing keyboard`() {
        val text = editTelegramMessageText { inlineMessageId = "inline-id"; this.text = "New" }
        assertEquals("inline-id", text.inlineMessageId)
        assertNull(text.chatId)
        val remove = editTelegramMessageReplyMarkup { inlineMessageId = "inline-id"; removeKeyboard() }
        assertTrue(remove.replyMarkup.keyboard.isEmpty())
        assertNull(remove.chatId)
        val bot = bot()
        // The normal response handler must not add a chat ID to an inline edit.
        val handler = io.chengine.method.PartialApiMethodReturnValueHandler()
        val context = io.chengine.connector.DefaultBotRequestContext()
        val response = io.chengine.connector.DefaultBotResponseContext()
        val method = io.chengine.method.HandlerMethod(EditMessageHandler(), EditMessageHandler::start)
        handler.process(text, method, context, response)
        handler.process(remove, method, context, response)
        assertNull(text.chatId)
        assertNull(remove.chatId)
        assertNull(bot.result)
    }
    @Test fun `rejects missing or conflicting edit targets and keyboard actions`() {
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageText { text = "text" } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageText { messageId = 1; inlineMessageId = "id"; text = "text" } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageText { messageId = 1; text = " " } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageReplyMarkup { messageId = 1 } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageReplyMarkup { messageId = 1; removeKeyboard(); inlineKeyboard {} } }
    }
}
