package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendAudio

fun sendTelegramAudio(block: SendTelegramAudioBuilder.() -> Unit) = SendTelegramAudioBuilder().apply(block)._build()

class SendTelegramAudioBuilder {
    fun _build() = SendAudio().apply {
        TODO()
    }
}
