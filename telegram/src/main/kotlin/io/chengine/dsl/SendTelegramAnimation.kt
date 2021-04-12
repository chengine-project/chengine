package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendAnimation

fun sendTelegramAnimation(block: SendTelegramAnimationBuilder.() -> Unit) =
    SendTelegramAnimationBuilder().apply(block)._build()

class SendTelegramAnimationBuilder {
    fun _build() = SendAnimation().apply {
        TODO()
    }
}