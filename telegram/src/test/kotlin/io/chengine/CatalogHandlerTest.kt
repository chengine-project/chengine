package io.chengine

import example.catalog.CatalogHandler
import io.chengine.connector.BotResponseContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

private class CatalogBot : ChengineTelegramLongPoolingBot() {
    var result: Any? = null
    override fun executeResponse(response: BotResponseContext) { result = response.currentResponseObject() }
}
class CatalogHandlerTest {
    private fun bot() = CatalogBot().also { Chengine(ChengineConfig(listOf(CatalogHandler()), listOf(it))) }
    private fun callback(data: String) = Update().apply { callbackQuery = CallbackQuery().apply {
        id = "query"; this.data = data
        message = Message().apply { messageId = 42; chat = Chat(123, "private") }
    } }
    private fun labels(markup: InlineKeyboardMarkup) = markup.keyboard.map { row -> row.map { it.text } }

    @Test fun `initial page offers next details and documentation`() {
        val message = CatalogHandler().start()
        assertTrue(message.text.contains("1/3"))
        assertEquals(listOf(listOf("Вперёд ➡️"), listOf("Подробнее"), listOf("Документация")), labels(message.replyMarkup as InlineKeyboardMarkup))
    }
    @Test fun `navigation details and return edit same message`() {
        val bot = bot()
        bot.process(callback("catalog:page:1"))
        val result = bot.result as List<*>
        assertTrue(result[0] is AnswerCallbackQuery)
        val edit = result[1] as EditMessageText
        assertEquals("123", edit.chatId)
        assertEquals(42, edit.messageId)
        assertTrue(edit.text.contains("2/3"))
        assertEquals(listOf("⬅️ Назад", "Вперёд ➡️"), labels(edit.replyMarkup)[0])
        bot.process(callback("catalog:item:1"))
        assertTrue(((bot.result as List<*>)[1] as EditMessageText).text.contains("Листовой"))
        bot.process(callback("catalog:page:2"))
        val last = (bot.result as List<*>)[1] as EditMessageText
        assertTrue(last.text.contains("3/3"))
        assertEquals(listOf("⬅️ Назад"), labels(last.replyMarkup)[0])
        bot.process(callback("catalog:page:1"))
        assertTrue(((bot.result as List<*>)[1] as EditMessageText).text.contains("2/3"))
    }
    @Test fun `invalid callback is acknowledged without editing`() {
        val bot = bot()
        listOf("catalog:page:-1", "catalog:item:3", "catalog:page:not-a-number", "unknown").forEach { data ->
            bot.process(callback(data))
            val result = bot.result as List<*>
            assertEquals(1, result.size)
            assertEquals(true, (result[0] as AnswerCallbackQuery).showAlert)
        }
    }
}
