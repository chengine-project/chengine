package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendSticker

fun sendTelegramSticker(block: SendTelegramStickerBuilder.() -> Unit) = SendTelegramStickerBuilder().apply(block)._build()

class  SendTelegramStickerBuilder {
    fun _build() = SendSticker().apply {
        TODO()
    }
}
