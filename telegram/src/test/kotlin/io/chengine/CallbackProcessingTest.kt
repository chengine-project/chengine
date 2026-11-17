package io.chengine

import io.chengine.command.HandleCommand
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleCallback
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

@Handler
class CallbackTestHandler {
    @HandleCommand("/start") fun start() = sendTelegramMessage {
        text = "Press"
        inlineKeyboard { row { button { text = "Check"; callbackData = "check_button" } } }
    }
    @HandleCommand("/command") fun command(update: Update) = AnswerCallbackQuery().apply {
        callbackQueryId = update.callbackQuery.id
        text = "command"
    }
    @TelegramHandleCallback fun callback(update: Update) = AnswerCallbackQuery().apply {
        callbackQueryId = update.callbackQuery.id
        text = "callback: ${update.callbackQuery.data}"
    }
}

private class CallbackBot : ChengineTelegramLongPoolingBot() {
    var result: Any? = null
    override fun executeResponse(response: BotResponseContext) { result = response.currentResponseObject() }
}

class CallbackProcessingTest {
    private fun bot() = CallbackBot().also { Chengine(ChengineConfig(listOf(CallbackTestHandler()), listOf(it))) }
    private fun callback(data: String) = Update().apply {
        callbackQuery = CallbackQuery().apply { id = "query-id"; this.data = data }
    }
    @Test fun `start constructs inline keyboard`() {
        val method = CallbackTestHandler().start()
        val keyboard = method.replyMarkup as InlineKeyboardMarkup
        assertEquals("Check", keyboard.keyboard[0][0].text)
        assertEquals("check_button", keyboard.keyboard[0][0].callbackData)
    }
    @Test fun `callback data reaches handler and is acknowledged`() {
        val bot = bot()
        bot.process(callback("check_button"))
        val response = bot.result as AnswerCallbackQuery
        assertEquals("query-id", response.callbackQueryId)
        assertEquals("callback: check_button", response.text)
    }
    @Test fun `registered callback command takes priority over generic handler`() {
        val bot = bot()
        bot.process(callback("/command"))
        assertEquals("command", (bot.result as AnswerCallbackQuery).text)
    }
    @Test fun `unknown callback command falls back to generic handler`() {
        val bot = bot()
        bot.process(callback("/unknown"))
        assertEquals("callback: /unknown", (bot.result as AnswerCallbackQuery).text)
    }
}
