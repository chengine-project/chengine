package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile

fun sendTelegramVideo(block: SendTelegramVideoBuilder.() -> Unit): SendVideo =
    SendTelegramVideoBuilder().apply(block)._build()

class SendTelegramVideoBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {
    var chatId: String? = null
    var video: InputFile? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    var duration: Int? = null
    var width: Int? = null
    var height: Int? = null
    var supportsStreaming: Boolean? = null
    var hasSpoiler: Boolean? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null

    fun video(block: VideoBuilder.() -> Unit) {
        video = VideoBuilder().apply(block)._build()
    }

    fun _build(): SendVideo = SendVideo.builder()
        .chatId(chatId ?: "")
        .video(requireNotNull(video) { "Video is required" })
        .build().apply {
            caption = this@SendTelegramVideoBuilder.caption
            parseMode = this@SendTelegramVideoBuilder.parseMode?.name
            duration = this@SendTelegramVideoBuilder.duration
            width = this@SendTelegramVideoBuilder.width
            height = this@SendTelegramVideoBuilder.height
            supportsStreaming = this@SendTelegramVideoBuilder.supportsStreaming
            hasSpoiler = this@SendTelegramVideoBuilder.hasSpoiler
            disableNotification = this@SendTelegramVideoBuilder.disableNotification
            replyToMessageId = this@SendTelegramVideoBuilder.replyToMessageId
            allowSendingWithoutReply = this@SendTelegramVideoBuilder.allowSendingWithoutReply
            replyMarkup = this@SendTelegramVideoBuilder.inlineKeyboard.toReplyMarkup()
        }
}
class VideoBuilder : TelegramInputFileBuilder()
