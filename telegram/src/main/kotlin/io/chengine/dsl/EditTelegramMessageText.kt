package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

fun editTelegramMessageText(block: EditTelegramMessageTextBuilder.() -> Unit): EditMessageText =
    EditTelegramMessageTextBuilder().apply(block)._build()

class EditTelegramMessageTextBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: Long? = null
    var messageId: Int? = null
    var inlineMessageId: String? = null
    var text: String? = null
    var parseMode: ParseMode? = null

    fun _build(): EditMessageText {
        validateEditTarget(chatId, messageId, inlineMessageId)
        val content = requireNotNull(text) { "Message text is required" }
        require(content.isNotBlank()) { "Message text must not be blank" }
        return EditMessageText.builder().build().apply {
            this.chatId = this@EditTelegramMessageTextBuilder.chatId?.toString()
            this.messageId = this@EditTelegramMessageTextBuilder.messageId
            this.inlineMessageId = this@EditTelegramMessageTextBuilder.inlineMessageId
            this.text = content
            this.parseMode = this@EditTelegramMessageTextBuilder.parseMode?.apiValue
            this.replyMarkup = inlineKeyboard.toReplyMarkup()
        }
    }
}

internal fun validateEditTarget(chatId: Long?, messageId: Int?, inlineMessageId: String?) {
    if (inlineMessageId != null) {
        require(inlineMessageId.isNotBlank()) { "Inline message ID must not be blank" }
        require(chatId == null && messageId == null) { "Inline message ID cannot be combined with chat or message ID" }
    } else {
        require(messageId != null && messageId > 0) { "A positive message ID is required" }
    }
}
