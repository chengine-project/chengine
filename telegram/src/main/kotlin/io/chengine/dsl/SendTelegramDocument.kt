package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendDocument

fun sendTelegramDocument(block: SendTelegramDocumentBuilder.() -> Unit): SendDocument {
    return SendTelegramDocumentBuilder().apply(block)._build()
}

class SendTelegramDocumentBuilder {
    fun _build(): SendDocument {
        val sendDocument = SendDocument()

        return sendDocument
    }
}