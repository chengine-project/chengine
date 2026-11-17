package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.objects.InputFile

fun sendTelegramSticker(block: SendTelegramStickerBuilder.() -> Unit): SendSticker =
    SendTelegramStickerBuilder().apply(block)._build()

class SendTelegramStickerBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: String? = null
    var sticker: InputFile? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null
    var emoji: String? = null
    fun sticker(block: TelegramInputFileBuilder.() -> Unit) { sticker = TelegramInputFileBuilder().apply(block)._build() }
    fun _build(): SendSticker = SendSticker(chatId ?: "", requireNotNull(sticker) { "Sticker is required" }).apply {
        disableNotification = this@SendTelegramStickerBuilder.disableNotification
        replyToMessageId = this@SendTelegramStickerBuilder.replyToMessageId
        allowSendingWithoutReply = this@SendTelegramStickerBuilder.allowSendingWithoutReply
        emoji = this@SendTelegramStickerBuilder.emoji
        replyMarkup = inlineKeyboard.toReplyMarkup()
    }
}
