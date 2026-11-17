package io.chengine

import io.chengine.command.HandleCommand
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.*
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.message.Message
import java.io.ByteArrayInputStream
import java.io.File

@Handler
class ExtraMediaTestHandler {
    @HandleCommand("/sticker") fun sticker() = sendTelegramSticker { sticker { name = "sticker-id" } }
    @HandleCommand("/animation") fun animation() = sendTelegramAnimation { animation { name = "animation-id" }; caption = "animation" }
    @HandleCommand("/caption") fun caption() = editTelegramMessageCaption { messageId = 42; caption = "new" }
    @HandleCommand("/media") fun media() = editTelegramMedia { messageId = 42; media { type = TelegramMediaType.PHOTO; name = "photo-id" } }
    @HandleCommand("/inline") fun inline() = editTelegramMessageCaption { inlineMessageId = "inline"; caption = "new" }
}
private class ExtraMediaBot : ChengineTelegramLongPoolingBot() {
    var result: Any? = null
    override fun executeResponse(response: BotResponseContext) { result = response.currentResponseObject() }
}
class ExtraMediaDslTest {
    private fun bot() = ExtraMediaBot().also { Chengine(ChengineConfig(listOf(ExtraMediaTestHandler()), listOf(it))) }
    private fun update(text: String) = Update().apply { message = Message().apply { this.text = text; chat = Chat(123, "private") } }
    @Test fun `routes new methods and fills chat ids`() {
        val bot = bot()
        bot.process(update("/sticker")); assertEquals("123", (bot.result as SendSticker).chatId)
        bot.process(update("/animation")); assertEquals("123", (bot.result as SendAnimation).chatId)
        bot.process(update("/caption")); assertEquals("123", (bot.result as EditMessageCaption).chatId)
        bot.process(update("/media")); assertEquals("123", (bot.result as EditMessageMedia).chatId)
        bot.process(update("/inline")); assertNull((bot.result as EditMessageCaption).chatId)
    }
    @Test fun `supports file id file and named stream for sends and media edits`() {
        assertEquals("id", sendTelegramSticker { sticker { name = "id" } }.sticker.attachName)
        assertNotNull(sendTelegramAnimation { animation { file = File("test.mp4") } }.animation.newMediaFile)
        val edit = editTelegramMedia { messageId = 5; media { type = TelegramMediaType.PHOTO; file = File("test.jpg"); caption = "new" } }
        assertTrue(edit.media is InputMediaPhoto)
        assertEquals("new", edit.media.caption)
        assertTrue(edit.media.isNewMedia)
        val stream = editTelegramMedia { messageId = 5; media { type = TelegramMediaType.VIDEO; name = "v.mp4"; mediaStream = ByteArrayInputStream(byteArrayOf(1)) } }
        assertNotNull(stream.media.newMediaStream)
        val inline = editTelegramMedia { inlineMessageId = "inline"; media { type = TelegramMediaType.DOCUMENT; name = "file-id" } }
        assertFalse(inline.media.isNewMedia)
    }
    @Test fun `rejects missing input and invalid edit targets`() {
        assertThrows(IllegalArgumentException::class.java) { sendTelegramSticker {} }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramAnimation { animation { mediaStream = ByteArrayInputStream(byteArrayOf(1)) } } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMessageCaption { caption = "text" } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMedia { messageId = 1 } }
        assertThrows(IllegalArgumentException::class.java) { editTelegramMedia { inlineMessageId = "inline"; media { type = TelegramMediaType.PHOTO; file = File("x.jpg") } } }
    }
}
