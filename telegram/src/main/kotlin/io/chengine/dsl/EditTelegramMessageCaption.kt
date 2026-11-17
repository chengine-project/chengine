package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption

fun editTelegramMessageCaption(block: EditTelegramMessageCaptionBuilder.() -> Unit): EditMessageCaption =
    EditTelegramMessageCaptionBuilder().apply(block)._build()

class EditTelegramMessageCaptionBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: Long? = null
    var messageId: Int? = null
    var inlineMessageId: String? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    fun _build(): EditMessageCaption {
        validateEditTarget(chatId, messageId, inlineMessageId)
        return EditMessageCaption().apply {
            chatId = this@EditTelegramMessageCaptionBuilder.chatId?.toString()
            messageId = this@EditTelegramMessageCaptionBuilder.messageId
            inlineMessageId = this@EditTelegramMessageCaptionBuilder.inlineMessageId
            caption = this@EditTelegramMessageCaptionBuilder.caption
            parseMode = this@EditTelegramMessageCaptionBuilder.parseMode?.apiValue
            replyMarkup = inlineKeyboard.toReplyMarkup()
        }
    }
}
