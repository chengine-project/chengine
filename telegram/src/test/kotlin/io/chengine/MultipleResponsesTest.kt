package io.chengine

import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramMessage
import io.chengine.dsl.sendTelegramPhoto
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.lang.reflect.Proxy

@Handler
class MultipleResponsesHandler {
    @HandleCommand("/multiple") fun multiple() = listOf(
        sendTelegramMessage { text = "first" },
        sendTelegramPhoto { photo { name = "existing-file-id" }; caption = "photo" },
        sendTelegramMessage { text = "last"; chatId = "456" }
    )
    @HandleCommand("/empty") fun empty() = emptyList<SendMessage>()
    @HandleCommand("/invalid") fun invalid(): List<Any?> = listOf(sendTelegramMessage { text = "not sent" }, null)
}

class MultipleResponsesTest {
    private fun update(command: String) = Update().apply { message = Message().apply {
        text = command; chat = Chat(123, "private")
    } }
    private fun setup(failOnSecond: Boolean = false): Pair<ChengineTelegramLongPoolingBot, MutableList<Any>> {
        val sent = mutableListOf<Any>()
        val client = Proxy.newProxyInstance(TelegramClient::class.java.classLoader, arrayOf(TelegramClient::class.java)) { _, method, args ->
            if (method.name != "execute" || args.isNullOrEmpty()) error("Unexpected call")
            val request = args[0]
            sent.add(request)
            if (failOnSecond && sent.size == 2) throw IllegalStateException("send failed")
            when (request) {
                is BotApiMethod<*>, is SendPhoto -> Message().apply { messageId = 1 }
                else -> error("Unexpected method")
            }
        } as TelegramClient
        val bot = ChengineTelegramLongPoolingBot()
        bot.telegramBotsLongPollingBotBot = TelegramBotsLongPoolingBot("test", "test", bot, client)
        Chengine(ChengineConfig(listOf(MultipleResponsesHandler()), listOf(bot)))
        return bot to sent
    }
    @Test fun `sends mixed responses in order and fills chat ids`() {
        val (bot, sent) = setup()
        bot.process(update("/multiple"))
        assertEquals(3, sent.size)
        assertEquals("first", (sent[0] as SendMessage).text)
        assertEquals("123", (sent[0] as SendMessage).chatId)
        assertEquals("photo", (sent[1] as SendPhoto).caption)
        assertEquals("123", (sent[1] as SendPhoto).chatId)
        assertEquals("last", (sent[2] as SendMessage).text)
        assertEquals("456", (sent[2] as SendMessage).chatId)
    }
    @Test fun `empty collection sends nothing and invalid list fails before any send`() {
        val (bot, sent) = setup()
        bot.process(update("/empty"))
        assertThrows(IllegalArgumentException::class.java) { bot.process(update("/invalid")) }
        assertTrue(sent.isEmpty())
    }
    @Test fun `send failure stops remaining responses`() {
        val (bot, sent) = setup(failOnSecond = true)
        assertThrows(IllegalStateException::class.java) { bot.process(update("/multiple")) }
        assertEquals(2, sent.size)
    }
}
