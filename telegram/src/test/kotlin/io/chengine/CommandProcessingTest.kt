package io.chengine

import io.chengine.command.CommandParameter
import io.chengine.command.HandleCommand
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleText
import io.chengine.text.HandleText
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update

@Handler
class TestHandler {
    @HandleCommand("/hello") fun hello(update: Update) = sendTelegramMessage { text = update.message?.text ?: "callback" }
    @HandleCommand("/item/id#") fun item(@CommandParameter("id") id: Int) = sendTelegramMessage { text = "item $id" }
    @HandleText("hi") fun text() = sendTelegramMessage { text = "exact text" }
    @TelegramHandleText fun otherText() = sendTelegramMessage { text = "fallback" }
}

@Handler class InvalidHandler { @HandleCommand("bad command") fun invalid() = "bad" }
@Handler class DuplicateHandler { @HandleCommand("/hello") fun duplicate() = "duplicate" }
@Handler class DuplicateTextHandler { @HandleText("/hello") fun duplicate() = "duplicate" }
@Handler class EmptyHandler { @HandleCommand("") fun empty() = "empty" }
@Handler class StringReplyHandler { @HandleCommand("/string") fun reply() = "Hello" }
@Handler class NoResponseHandler {
    var called = false
    @HandleCommand("/noresponse") fun noResponse() { called = true }
}

private class CapturingBot : ChengineTelegramLongPoolingBot() {
    var result: Any? = null
    override fun executeResponse(response: BotResponseContext) { result = response.currentResponseObject() }
}

class CommandProcessingTest {
    private fun bot(vararg handlers: Any): CapturingBot = CapturingBot().also {
        Chengine(ChengineConfig(handlers.toList(), listOf(it)))
    }
    private fun update(text: String): Update = Update().apply {
        message = Message().apply { this.text = text; chat = Chat(123L, "private") }
    }
    private fun response(bot: CapturingBot) = bot.result as SendMessage

    @Test fun `string handler becomes a message addressed to the request chat`() {
        val bot = bot(StringReplyHandler())
        bot.process(update("/string"))
        assertEquals("Hello", response(bot).text)
        assertEquals("123", response(bot).chatId)
    }

    @Test fun `unit handler completes without producing a response`() {
        val handler = NoResponseHandler()
        val bot = bot(handler)
        bot.process(update("/noresponse"))
        assertTrue(handler.called)
        assertNull(bot.result)
    }

    @Test fun `command with payload is routed and original update is retained`() {
        val bot = bot(TestHandler())
        bot.process(update("/hello payload"))
        assertEquals("/hello payload", response(bot).text)
        assertEquals("123", response(bot).chatId)
    }
    @Test fun `exact and general text handlers work for final Kotlin methods`() {
        val bot = bot(TestHandler())
        bot.process(update("hi")); assertEquals("exact text", response(bot).text)
        bot.process(update("other")); assertEquals("fallback", response(bot).text)
    }
    @Test fun `callback command routes and fills chat id`() {
        val bot = bot(TestHandler())
        val callback = CallbackQuery().apply { data = "/hello"; message = update("hi").message }
        bot.process(Update().apply { callbackQuery = callback })
        assertEquals("callback", response(bot).text)
        assertEquals("123", response(bot).chatId)
    }
    @Test fun `group commands route only to the addressed bot`() {
        val bot = bot(TestHandler())
        bot.telegramBotsLongPollingBotBot = TelegramBotsLongPoolingBot("test-token", "mybot", bot)
        bot.process(update("/hello@otherbot")); assertNull(bot.result)
        bot.process(update("/hello@mybot payload")); assertEquals("/hello@mybot payload", response(bot).text)
    }
    @Test fun `legacy command parameters are parsed`() {
        val bot = bot(TestHandler())
        bot.process(update("/item/id#42"))
        assertEquals("item 42", response(bot).text)
    }
    @Test fun `unknown and malformed commands are ignored`() {
        val bot = bot(TestHandler())
        bot.process(update("/missing")); assertNull(bot.result)
        bot.process(update("/item/id#1/id#2")); assertNull(bot.result)
    }
    @Test fun `invalid empty and duplicate commands fail during initialization`() {
        val invalid = assertThrows(IllegalArgumentException::class.java) { bot(InvalidHandler()) }
        assertTrue(invalid.message!!.contains("bad command"))
        assertThrows(IllegalArgumentException::class.java) { bot(EmptyHandler()) }
        assertThrows(IllegalArgumentException::class.java) { bot(TestHandler(), DuplicateHandler()) }
        assertThrows(RuntimeException::class.java) { bot(TestHandler(), DuplicateTextHandler()) }
    }
}
