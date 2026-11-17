package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineConfig
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramDocument
import io.chengine.dsl.sendTelegramPhoto
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File
import java.nio.file.Path

@Handler
class MediaTransportHandler(private val file: File) {
    @HandleCommand("/photoid") fun photoId() = sendTelegramPhoto { photo { name = "photo-id" } }
    @HandleCommand("/photofile") fun photoFile() = sendTelegramPhoto { photo { file = this@MediaTransportHandler.file }; caption = "image" }
    @HandleCommand("/docid") fun docId() = sendTelegramDocument { document { name = "doc-id" } }
    @HandleCommand("/docfile") fun docFile() = sendTelegramDocument { document { file = this@MediaTransportHandler.file }; caption = "file" }
}
class MediaTransportTest {
    @TempDir lateinit var dir: Path
    @Test fun `photo and document uploads and ids reach the client with chat id`() {
        val file = dir.resolve("media.txt").toFile().apply { writeText("media") }
        val client = mock(TelegramClient::class.java)
        val bot = ChengineTelegramLongPoolingBot()
        val adapter = TelegramBotsLongPoolingBot("test-token", "test_bot", bot, client)
        bot.telegramBotsLongPollingBotBot = adapter
        Chengine(ChengineConfig(listOf(MediaTransportHandler(file)), listOf(bot)))
        fun send(command: String) = adapter.consume(listOf(Update().apply { message = Message().apply { text = command; chat = Chat(42L, "private") } }))
        send("/photoid"); send("/photofile"); send("/docid"); send("/docfile")
        val photos = ArgumentCaptor.forClass(SendPhoto::class.java)
        val documents = ArgumentCaptor.forClass(SendDocument::class.java)
        verify(client, times(2)).execute(photos.capture()); verify(client, times(2)).execute(documents.capture())
        assertEquals(listOf("42", "42"), photos.allValues.map { it.chatId })
        assertEquals(listOf("42", "42"), documents.allValues.map { it.chatId })
        assertEquals("photo-id", photos.allValues[0].photo.attachName)
        assertEquals(file, photos.allValues[1].photo.newMediaFile)
        assertEquals("doc-id", documents.allValues[0].document.attachName)
        assertEquals(file, documents.allValues[1].document.newMediaFile)
        assertEquals("image", photos.allValues[1].caption)
        assertEquals("file", documents.allValues[1].caption)
    }
}
