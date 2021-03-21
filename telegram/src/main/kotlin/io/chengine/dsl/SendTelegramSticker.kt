package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.polls.SendPoll

fun sendTelegramPoll(block: SendTelegramPollBuilder.() -> Unit): SendPoll {
    return SendTelegramPollBuilder().apply(block)._build()
}

class  SendTelegramPollBuilder {
    fun _build(): SendPoll {
        TODO()
    }
}
