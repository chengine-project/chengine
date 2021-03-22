package io.chengine.dsl

import io.chengine.common.isNull
import io.chengine.common.then
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

inline fun sendTelegramMessage(block: SendTelegramMessageBuilder.() -> Unit): SendMessage {
    return SendTelegramMessageBuilder().apply(block)._build()
}

@DslMarker
annotation class SendTelegramMessageDsl

@SendTelegramMessageDsl
class SendTelegramMessageBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {

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

        val sendMessage = SendMessage()
        chatId?.let {
            sendMessage.chatId = it
        }
        sendMessage.text = text!!
        sendMessage.allowSendingWithoutReply = allowSendingWithoutReply
        sendMessage.disableWebPagePreview = disableWebPagePreview
        sendMessage.parseMode = parseMode?.toString()
        sendMessage.disableNotification = disableNotification
        sendMessage.replyToMessageId = replyToMessageId
        //sendMessage.replyMarkup = inlineKeyboard.toReplyMarkup()

        return sendMessage
    }
}