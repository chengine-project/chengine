package io.chengine.dsl

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice

fun sendTelegramInvoice(block: SendTelegramInvoiceBuilder.() -> Unit): SendInvoice {
    return SendTelegramInvoiceBuilder().apply(block)._build()
}

@Serializable
data class InvoicePayloadMetadata(val paymentIdentifier: String?, val payload: String?)

@DslMarker
annotation class SendTelegramInvoiceMarker

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
     * Payment identifier
     *
     * @see io.chengine.handler.payment.TelegramHandlePreCheckout
     * @see io.chengine.handler.payment.TelegramHandleSuccessPayment
     */
    var paymentIdentifier: String? = null

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

    private var prices: List<LabeledPrice>? = null
    fun prices(block: TelegramInvoicePriceBuilder.() -> Unit) {
        val invoicePriceBuilder = TelegramInvoicePriceBuilder()
        block.invoke(invoicePriceBuilder)
        prices = invoicePriceBuilder
    }

    fun _build(): SendInvoice {
        val sendInvoice = SendInvoice()

        chatId?.let {
            if (it.isNotBlank()) {
                sendInvoice.chatId = it
            }
        }

        title?.let {
            if (it.isNotBlank()) {
                sendInvoice.title = it
            }
        }

        description?.let {
            if (it.isNotBlank()) {
                sendInvoice.description = it
            }
        }

        InvoicePayloadMetadata(paymentIdentifier, payload).let {
            sendInvoice.payload = Json.encodeToString(serializer(), it)
        }

        currency?.let {
            if (it.isNotBlank()) {
                sendInvoice.currency = it
            }
        }

        providerToken?.let {
            if (it.isNotBlank()) {
                sendInvoice.providerToken = it
            }
        }

        prices?.let {
            sendInvoice.prices = it
        }

        return sendInvoice
    }
}

@SendTelegramInvoiceMarker
class TelegramInvoicePriceBuilder : ArrayList<LabeledPrice>() {

    fun price(block: LabeledPriceBuilder.() -> Unit): LabeledPrice {
        val labeledPriceBuilder = LabeledPriceBuilder()
        block.invoke(labeledPriceBuilder)
        return labeledPriceBuilder._build()
    }

    operator fun LabeledPrice.unaryPlus() {
        add(this)
    }
}

@SendTelegramInvoiceMarker
class LabeledPriceBuilder {

    var label: String? = null
    var amount: Int? = null

    fun _build(): LabeledPrice {
        val labeledPrice = LabeledPrice()
        labeledPrice.label = label!!
        labeledPrice.amount = amount!!
        return labeledPrice
    }
}