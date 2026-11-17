package io.chengine

import io.chengine.command.HandleCommand
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleMedia
import io.chengine.handler.TelegramHandleText
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.*
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.games.Animation
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.photo.PhotoSize
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker
import java.util.concurrent.CancellationException

@Handler
class MediaAndErrorHandler {
    @TelegramHandleMedia fun media(update: Update) = sendTelegramMessage { text = "media:${update.message.caption}" }
    @TelegramHandleText fun text() = sendTelegramMessage { text = "text" }
    @HandleCommand("/fail") fun fail(): SendMessage = throw IllegalStateException("test failure")
    @HandleCommand("/canceltest") fun cancel(): SendMessage = throw CancellationException("cancel")
    @HandleCommand("/ok") fun ok() = sendTelegramMessage { text = "ok" }
}

private class MediaCapturingBot : ChengineTelegramLongPoolingBot() {
    val messages = mutableListOf<SendMessage>()
    override fun executeResponse(response: BotResponseContext) { messages.add(response.currentResponseObject() as SendMessage) }
}

private class ErrorCapturingConsumer(bot: MediaCapturingBot) : TelegramBotsLongPoolingBot("test", "test", bot) {
    val failures = mutableListOf<Pair<Update, Exception>>()
    override fun onUpdateError(update: Update, exception: Exception) { failures.add(update to exception) }
}

class MediaAndUpdateErrorTest {
    private fun bot() = MediaCapturingBot().also { Chengine(ChengineConfig(listOf(MediaAndErrorHandler()), listOf(it))) }
    private fun update(message: Message) = Update().apply { this.message = message.apply { chat = Chat(123, "private") } }

    @Test fun `routes supported media including caption to media handler`() {
        val bot = bot()
        val messages = listOf(
            Message().apply { photo = listOf(PhotoSize()); caption = "photo" },
            Message().apply { document = Document() },
            Message().apply { video = Video() },
            Message().apply { audio = Audio() },
            Message().apply { voice = Voice() },
            Message().apply { animation = Animation("id", "unique", 1, 1, 1) },
            Message().apply { videoNote = VideoNote() },
            Message().apply { sticker = Sticker() }
        )
        messages.forEach { bot.process(update(it)) }
        assertEquals(messages.size, bot.messages.size)
        assertEquals("media:photo", bot.messages.first().text)
        assertTrue(bot.messages.all { it.chatId == "123" })
        bot.process(update(Message().apply { text = "hello" }))
        assertEquals("text", bot.messages.last().text)
    }

    @Test fun `failed update is reported and does not prevent next update`() {
        val bot = bot()
        val consumer = ErrorCapturingConsumer(bot)
        val failed = update(Message().apply { text = "/fail" })
        val next = update(Message().apply { text = "/ok" })
        consumer.consume(listOf(failed, next))
        assertEquals(1, consumer.failures.size)
        assertSame(failed, consumer.failures.single().first)
        assertEquals("ok", bot.messages.single().text)
    }

    @Test fun `cancellation is not swallowed`() {
        val consumer = ErrorCapturingConsumer(bot())
        assertThrows(CancellationException::class.java) {
            consumer.consume(listOf(update(Message().apply { text = "/canceltest" })))
        }
        assertTrue(consumer.failures.isEmpty())
    }
}
