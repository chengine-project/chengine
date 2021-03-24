package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText

fun editTelegramMessageText(block: EditTelegramMessageTextBuilder.() -> Unit): EditMessageText =
    EditTelegramMessageTextBuilder().apply(block)._build()

class EditTelegramMessageTextBuilder {

    var chatId: Long? = null
    var text: String? = null
    var parseMode: ParseMode? = null

    fun _build(): EditMessageText {
        return EditMessageText().apply {
            this@EditTelegramMessageTextBuilder.chatId?.let {
                this.chatId = it.toString()
            }
            this@EditTelegramMessageTextBuilder.text?.let {
                this.text = it
            }
            this@EditTelegramMessageTextBuilder.parseMode?.let {
                this.parseMode = it.name
            }
        }
    }
}