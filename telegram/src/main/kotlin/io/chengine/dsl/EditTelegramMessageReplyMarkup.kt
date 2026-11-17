package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

/** Replaces the entire inline keyboard, or removes it with [EditTelegramMessageReplyMarkupBuilder.removeKeyboard]. */
fun editTelegramMessageReplyMarkup(block: EditTelegramMessageReplyMarkupBuilder.() -> Unit): EditMessageReplyMarkup =
    EditTelegramMessageReplyMarkupBuilder().apply(block)._build()

class EditTelegramMessageReplyMarkupBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: Long? = null
    var messageId: Int? = null
    var inlineMessageId: String? = null
    private var remove = false

    fun removeKeyboard() { remove = true }

    fun _build(): EditMessageReplyMarkup {
        validateEditTarget(chatId, messageId, inlineMessageId)
        require(remove != (inlineKeyboard != null)) { "Specify a replacement keyboard or removeKeyboard(), but not both" }
        val markup = if (remove) InlineKeyboardMarkup.builder().keyboard(emptyList()).build() else inlineKeyboard.toReplyMarkup()
        return EditMessageReplyMarkup().apply {
            this.chatId = this@EditTelegramMessageReplyMarkupBuilder.chatId?.toString()
            this.messageId = this@EditTelegramMessageReplyMarkupBuilder.messageId
            this.inlineMessageId = this@EditTelegramMessageReplyMarkupBuilder.inlineMessageId
            this.replyMarkup = markup
        }
    }
}
