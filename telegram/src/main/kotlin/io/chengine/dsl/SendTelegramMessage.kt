package io.chengine.dsl

import io.chengine.common.isNull
import io.chengine.common.then
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

inline fun sendTelegramMessage(block: SendTelegramMessageBuilder.() -> Unit) = SendTelegramMessageBuilder().apply(block)._build()

@DslMarker
annotation class SendTelegramMessageDsl

@SendTelegramMessageDsl
class SendTelegramMessageBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter, TelegramReplyKeyboardMarkupSupporter {
    override var replyKeyboard: TelegramReplyKeyboard? = null

    var chatId: String? = null
    var text: String? = null
    var parseMode: ParseMode? = null
    var disableWebPagePreview: Boolean? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null

    fun _build(): SendMessage {
        text.isNull().then {
            throw RuntimeException("Message text can't be null")
        }

        val sendMessage = SendMessage.builder().chatId(chatId ?: "").text(text!!).build()
        chatId?.let {
            sendMessage.chatId = it
        }
        sendMessage.text = text!!
        sendMessage.allowSendingWithoutReply = allowSendingWithoutReply
        sendMessage.disableWebPagePreview = disableWebPagePreview
        sendMessage.parseMode = parseMode?.toString()
        sendMessage.disableNotification = disableNotification
        sendMessage.replyToMessageId = replyToMessageId
        require(inlineKeyboard == null || replyKeyboard == null) { "Choose either inline or reply keyboard" }
        sendMessage.replyMarkup = inlineKeyboard.toReplyMarkup() ?: replyKeyboard.toReplyMarkup()

        return sendMessage
    }
}