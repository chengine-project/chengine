package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVideo

fun sendTelegramVideo(block: SendTelegramVideoBuilder.() -> Unit): SendVideo {
    return SendTelegramVideoBuilder().apply(block)._build()
}

class SendTelegramVideoBuilder {
    fun _build(): SendVideo {
        TODO()
    }
}