package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineConfig
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

@Handler
class TransportHandler {
    @HandleCommand("/start") fun start() = sendTelegramMessage { text = "hello" }
    @HandleCommand("/file") fun file() = SendDocument.builder().chatId("").document(InputFile("file-id")).build()
}

class TelegramTransportTest {
    @Test fun `long polling forwards update and sends response through client`() {
        val client = mock(TelegramClient::class.java)
        val bot = ChengineTelegramLongPoolingBot()
        val adapter = TelegramBotsLongPoolingBot("test-token", "test_bot", bot, client)
        bot.telegramBotsLongPollingBotBot = adapter
        Chengine(ChengineConfig(listOf(TransportHandler()), listOf(bot)))
        val update = Update().apply { message = Message().apply { text = "/start"; chat = Chat(42L, "private") } }

        adapter.consume(listOf(update))

        val response = ArgumentCaptor.forClass(SendMessage::class.java)
        verify(client).execute(response.capture())
        assertEquals("42", response.value.chatId)
        assertEquals("hello", response.value.text)

        update.message.text = "/file"
        adapter.consume(listOf(update))
        val document = ArgumentCaptor.forClass(SendDocument::class.java)
        verify(client).execute(document.capture())
        assertEquals("42", document.value.chatId)
    }
}
