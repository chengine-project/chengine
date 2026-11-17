package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.objects.media.*
import java.io.File
import java.io.InputStream

fun editTelegramMedia(block: EditTelegramMessageMediaBuilder.() -> Unit): EditMessageMedia =
    EditTelegramMessageMediaBuilder().apply(block)._build()

class EditTelegramMessageMediaBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: Long? = null
    var messageId: Int? = null
    var inlineMessageId: String? = null
    var media: InputMedia? = null
    fun media(block: MediaBuilder.() -> Unit) { media = MediaBuilder().apply(block)._build() }
    fun _build(): EditMessageMedia {
        validateEditTarget(chatId, messageId, inlineMessageId)
        val value = requireNotNull(media) { "Media is required" }
        require(inlineMessageId == null || !value.isNewMedia) { "Inline messages cannot upload new media; use a file ID or URL" }
        return EditMessageMedia(value).apply {
            chatId = this@EditTelegramMessageMediaBuilder.chatId?.toString()
            messageId = this@EditTelegramMessageMediaBuilder.messageId
            inlineMessageId = this@EditTelegramMessageMediaBuilder.inlineMessageId
            replyMarkup = inlineKeyboard.toReplyMarkup()
        }
    }
}

enum class TelegramMediaType { PHOTO, VIDEO, ANIMATION, AUDIO, DOCUMENT }

class MediaBuilder {
    var type: TelegramMediaType? = null
    var name: String? = null
    var file: File? = null
    var mediaStream: InputStream? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    fun _build(): InputMedia {
        require(name == null || name!!.isNotBlank()) { "File name or identifier must not be blank" }
        require(file == null || mediaStream == null) { "Choose either a file or a stream" }
        val result = when (requireNotNull(type) { "Media type is required" }) {
            TelegramMediaType.PHOTO -> InputMediaPhoto("")
            TelegramMediaType.VIDEO -> InputMediaVideo("")
            TelegramMediaType.ANIMATION -> InputMediaAnimation("")
            TelegramMediaType.AUDIO -> InputMediaAudio("")
            TelegramMediaType.DOCUMENT -> InputMediaDocument("")
        }
        when {
            file != null -> result.setMedia(file, name ?: file!!.name)
            mediaStream != null && name != null -> result.setMedia(mediaStream, name)
            mediaStream != null -> throw IllegalArgumentException("A stream requires a file name")
            name != null -> result.setMedia(name)
            else -> throw IllegalArgumentException("A file, stream, or Telegram file_id is required")
        }
        result.caption = caption
        result.parseMode = parseMode?.name
        return result
    }
}
