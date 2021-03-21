package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption

fun editTelegramMessageCaption(block: EditTelegramMessageCaptionBuilder.() -> Unit): EditMessageCaption {
    return EditTelegramMessageCaptionBuilder().apply(block)._build()
}

class EditTelegramMessageCaptionBuilder {
    fun _build(): EditMessageCaption {
        TODO()
    }
}