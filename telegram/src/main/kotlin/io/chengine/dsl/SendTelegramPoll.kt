package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.polls.SendPoll

fun sendTelegramPoll(block: SendTelegramPollBuilder.() -> Unit) = SendTelegramPollBuilder().also(block)._build()

class SendTelegramPollBuilder {

    fun _build() = SendPoll().apply {
        TODO()
    }

}
