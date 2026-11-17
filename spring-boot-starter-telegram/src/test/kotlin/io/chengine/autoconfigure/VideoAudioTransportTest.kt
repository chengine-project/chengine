package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineConfig
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramAudio
import io.chengine.dsl.sendTelegramVideo
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.telegram.telegrambots.meta.api.methods.send.SendAudio
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.io.File
import java.nio.file.Path

@Handler
class VideoAudioTransportHandler(private val file: File) {
    @HandleCommand("/videoid") fun videoId() = sendTelegramVideo { video { name = "video-id" } }
    @HandleCommand("/videofile") fun videoFile() = sendTelegramVideo { video { file = this@VideoAudioTransportHandler.file }; caption = "video" }
    @HandleCommand("/audioid") fun audioId() = sendTelegramAudio { audio { name = "audio-id" } }
    @HandleCommand("/audiofile") fun audioFile() = sendTelegramAudio { audio { file = this@VideoAudioTransportHandler.file }; title = "song" }
}
class VideoAudioTransportTest {
    @TempDir lateinit var dir: Path
    @Test fun `video and audio uploads and ids reach client with chat id`() {
        val file = dir.resolve("media.bin").toFile().apply { writeText("media") }
        val client = mock(TelegramClient::class.java)
        val bot = ChengineTelegramLongPoolingBot()
        val adapter = TelegramBotsLongPoolingBot("test-token", "test_bot", bot, client)
        bot.telegramBotsLongPollingBotBot = adapter
        Chengine(ChengineConfig(listOf(VideoAudioTransportHandler(file)), listOf(bot)))
        fun send(command: String) = adapter.consume(listOf(Update().apply { message = Message().apply { text = command; chat = Chat(42L, "private") } }))
        send("/videoid"); send("/videofile"); send("/audioid"); send("/audiofile")
        val videos = ArgumentCaptor.forClass(SendVideo::class.java)
        val audios = ArgumentCaptor.forClass(SendAudio::class.java)
        verify(client, times(2)).execute(videos.capture()); verify(client, times(2)).execute(audios.capture())
        assertEquals(listOf("42", "42"), videos.allValues.map { it.chatId })
        assertEquals(listOf("42", "42"), audios.allValues.map { it.chatId })
        assertEquals("video-id", videos.allValues[0].video.attachName)
        assertEquals(file, videos.allValues[1].video.newMediaFile)
        assertEquals("audio-id", audios.allValues[0].audio.attachName)
        assertEquals(file, audios.allValues[1].audio.newMediaFile)
        assertEquals("video", videos.allValues[1].caption)
        assertEquals("song", audios.allValues[1].title)
    }
}
