package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendInvoice

fun sendTelegramInvoice(block: SendTelegramInvoiceBuilder.() -> Unit): SendInvoice {
    return SendTelegramInvoiceBuilder().apply(block)._build()
}

class SendTelegramInvoiceBuilder {
    fun _build(): SendInvoice {
        TODO()
    }
}