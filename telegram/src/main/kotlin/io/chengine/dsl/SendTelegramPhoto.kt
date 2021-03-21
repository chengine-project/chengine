package io.chengine.dsl

import io.chengine.common.isNull
import io.chengine.message.Send

fun sendTelegramPhoto(block: SendTelegramPhotoBuilder.() -> Unit): SendTelegramPhoto {
    return SendTelegramPhotoBuilder().apply(block).build()
}

class SendTelegramPhotoBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {

    var chatId: String? = null
    var photo: TelegramMediaFile? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    var allowSendingWithoutReply: Boolean? = null

    fun build(): SendTelegramPhoto {
        photo.isNull {
            throw RuntimeException("Photo can't be null")
        }

        return SendTelegramPhoto(
            chatId = chatId,
            photo = photo!!,
            disableNotification = disableNotification,
            replyToMessageId = replyToMessageId,
            caption = caption,
            parseMode = parseMode,
            allowSendingWithoutReply = allowSendingWithoutReply,
            inlineKeyboard = inlineKeyboard
        )
    }
}

data class SendTelegramPhoto(
    val chatId: String?,
    val caption: String?,
    val parseMode: ParseMode?,
    val photo: TelegramMediaFile,
    val inlineKeyboard: TelegramInlineKeyboard?,
    val allowSendingWithoutReply: Boolean?,
    val disableNotification: Boolean?,
    val replyToMessageId: Int?
) : Send