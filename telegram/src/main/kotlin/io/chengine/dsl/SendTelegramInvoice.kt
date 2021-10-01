package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendInvoice

fun sendTelegramInvoice(block: SendTelegramInvoiceBuilder.() -> Unit): SendInvoice {
    return SendTelegramInvoiceBuilder().apply(block)._build()
}

class SendTelegramInvoiceBuilder {

    /**
     * Chat identifier
     */
    var chatId: String? = null

    /**
     * Product name
     */
    var title: String? = null

    /**
     * Product description
     */
    var description: String? = null

    /**
     *
     */
    var currency: String? = null
        set(value) {
            if (value == null)
                throw RuntimeException("Currency can't be null. Please enter required ISO 4217 currency code")
            else field = value
        }

    /**
     * Bot-defined invoice payload, 1-128 bytes. This will not be displayed to the user, use for your internal processes.
     */
    var payload: String? = null
    var providerToken: String? = null

    fun _build(): SendInvoice {
        val sendInvoice = SendInvoice()
        chatId?.let {
            if (it.isNotBlank()) {
                sendInvoice.chatId = it
            }
        }

        sendInvoice.title = title!!
        sendInvoice.description = description!!
        sendInvoice.payload = payload!!
        sendInvoice.currency = currency!!
        sendInvoice.providerToken = providerToken!!

        return sendInvoice
    }
}