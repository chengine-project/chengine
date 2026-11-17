package io.chengine.agent

import io.chengine.connector.DefaultBotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.session.SessionKey
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.lang.reflect.Proxy

private class FakeModel {
    fun respond(tool: TelegramSendMessageTool): Int {
        assertEquals("send_telegram_message", tool.name)
        return tool.execute("Привет от тестовой модели!")
    }
}

class TelegramSendMessageToolTest {
    private fun request(chat: String? = "123") = DefaultBotRequestContext().apply {
        botApiIdentifier = TelegramBotApiIdentifier.instance
        sessionKey = SessionKey("1", chat, "telegram", "bot")
    }

    private fun client(send: (SendMessage) -> Message): TelegramClient = Proxy.newProxyInstance(
        TelegramClient::class.java.classLoader, arrayOf(TelegramClient::class.java)
    ) { _, method, args ->
        if (method.name == "execute" && args?.singleOrNull() is SendMessage) send(args[0] as SendMessage)
        else error("Unexpected client call: ${method.name}")
    } as TelegramClient

    @Test fun `fake model sends text to request chat and receives message id`() {
        val sent = mutableListOf<SendMessage>()
        val tool = TelegramSendMessageTool(request(), client { message ->
            sent.add(message)
            Message().apply { messageId = 42 }
        })
        assertEquals(42, FakeModel().respond(tool))
        assertEquals(1, sent.size)
        assertEquals("123", sent.single().chatId)
        assertEquals("Привет от тестовой модели!", sent.single().text)
    }

    @Test fun `requires chat and nonblank text`() {
        var calls = 0
        val client = client { calls++; Message().apply { messageId = 1 } }
        assertThrows(IllegalArgumentException::class.java) { TelegramSendMessageTool(request(null), client) }
        assertThrows(IllegalArgumentException::class.java) { TelegramSendMessageTool(request(" "), client) }
        assertThrows(IllegalArgumentException::class.java) { TelegramSendMessageTool(request(), client).execute("  ") }
        assertEquals(0, calls)
    }

    @Test fun `propagates delivery errors`() {
        val tool = TelegramSendMessageTool(request(), client { throw IllegalStateException("send failed") })
        assertThrows(IllegalStateException::class.java) { tool.execute("Hi") }
    }
}
