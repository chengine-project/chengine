package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVideo

fun sendTelegramVideo(block: SendTelegramVideoBuilder.() -> Unit) = SendTelegramVideoBuilder().apply(block)._build()

class SendTelegramVideoBuilder {



    fun _build() = SendVideo().apply {
        TODO()
    }
}