package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation
import org.telegram.telegrambots.meta.api.objects.InputFile

fun sendTelegramAnimation(block: SendTelegramAnimationBuilder.() -> Unit): SendAnimation =
    SendTelegramAnimationBuilder().apply(block)._build()

class SendTelegramAnimationBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: String? = null
    var animation: InputFile? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null
    fun animation(block: TelegramInputFileBuilder.() -> Unit) { animation = TelegramInputFileBuilder().apply(block)._build() }
    fun _build(): SendAnimation = SendAnimation(chatId ?: "", requireNotNull(animation) { "Animation is required" }).apply {
        caption = this@SendTelegramAnimationBuilder.caption
        parseMode = this@SendTelegramAnimationBuilder.parseMode?.name
        disableNotification = this@SendTelegramAnimationBuilder.disableNotification
        replyToMessageId = this@SendTelegramAnimationBuilder.replyToMessageId
        allowSendingWithoutReply = this@SendTelegramAnimationBuilder.allowSendingWithoutReply
        replyMarkup = inlineKeyboard.toReplyMarkup()
    }
}
