package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote

fun sendTelegramVideoNote(block: SendTelegramVideoNoteBuilder.() -> Unit) =
    SendTelegramVideoNoteBuilder().apply(block)._build()

class SendTelegramVideoNoteBuilder {
    fun _build() = SendVideoNote().apply {
        TODO()
    }
}