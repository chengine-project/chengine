package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.File
import java.io.InputStream

fun sendTelegramPhoto(block: SendTelegramPhotoBuilder.() -> Unit): SendPhoto {
    return SendTelegramPhotoBuilder().apply(block)._build()
}

class SendTelegramPhotoBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {

    var chatId: String? = null
    var photo: InputFile? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    var allowSendingWithoutReply: Boolean? = null

    fun _build() = SendPhoto().apply {
        this@SendTelegramPhotoBuilder.photo?.let { photo = it }
        this@SendTelegramPhotoBuilder.disableNotification?.let { disableNotification = it }
        this@SendTelegramPhotoBuilder.replyToMessageId?.let { replyToMessageId = it }
        this@SendTelegramPhotoBuilder.parseMode?.let { parseMode = it.name }
        this@SendTelegramPhotoBuilder.caption?.let { caption = it }
        this@SendTelegramPhotoBuilder.allowSendingWithoutReply?.let { allowSendingWithoutReply = it }
        replyMarkup = this@SendTelegramPhotoBuilder.inlineKeyboard?.toReplyMarkup()
    }

    fun photo(block: PhotoBuilder.() -> Unit) {
        photo = PhotoBuilder().apply(block)._build()
    }

}

class PhotoBuilder {

    var name: String? = null
    var file: File? = null
    var mediaStream: InputStream? = null

    fun _build(): InputFile {
        if (name != null && file == null && mediaStream == null) {
            return InputFile(name)
        }
        if (name == null && file != null && mediaStream == null) {
            return InputFile(file)
        }
        if (name != null && file != null && mediaStream == null) {
            return InputFile(file, name)
        }
        if (name != null && file == null && mediaStream != null) {
            return InputFile(mediaStream, name)
        }

        throw RuntimeException("Incorrect photo block!")
    }
}