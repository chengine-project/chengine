package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.InputFile

fun sendTelegramDocument(block: SendTelegramDocumentBuilder.() -> Unit): SendDocument =
    SendTelegramDocumentBuilder().apply(block)._build()

class SendTelegramDocumentBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {
    var chatId: String? = null
    var document: InputFile? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null
    var disableContentTypeDetection: Boolean? = null

    fun document(block: DocumentBuilder.() -> Unit) {
        document = DocumentBuilder().apply(block)._build()
    }

    fun _build(): SendDocument = SendDocument.builder()
        .chatId(chatId ?: "")
        .document(requireNotNull(document) { "Document is required" })
        .build().apply {
            caption = this@SendTelegramDocumentBuilder.caption
            parseMode = this@SendTelegramDocumentBuilder.parseMode?.apiValue
            disableNotification = this@SendTelegramDocumentBuilder.disableNotification
            replyToMessageId = this@SendTelegramDocumentBuilder.replyToMessageId
            allowSendingWithoutReply = this@SendTelegramDocumentBuilder.allowSendingWithoutReply
            disableContentTypeDetection = this@SendTelegramDocumentBuilder.disableContentTypeDetection
            replyMarkup = this@SendTelegramDocumentBuilder.inlineKeyboard.toReplyMarkup()
        }
}

class DocumentBuilder : TelegramInputFileBuilder()
