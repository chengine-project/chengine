package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendAudio
import org.telegram.telegrambots.meta.api.objects.InputFile

fun sendTelegramAudio(block: SendTelegramAudioBuilder.() -> Unit): SendAudio =
    SendTelegramAudioBuilder().apply(block)._build()

class SendTelegramAudioBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {
    var chatId: String? = null
    var audio: InputFile? = null
    var caption: String? = null
    var parseMode: ParseMode? = null
    var duration: Int? = null
    var performer: String? = null
    var title: String? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null

    fun audio(block: AudioBuilder.() -> Unit) {
        audio = AudioBuilder().apply(block)._build()
    }

    fun _build(): SendAudio = SendAudio.builder()
        .chatId(chatId ?: "")
        .audio(requireNotNull(audio) { "Audio is required" })
        .build().apply {
            caption = this@SendTelegramAudioBuilder.caption
            parseMode = this@SendTelegramAudioBuilder.parseMode?.apiValue
            duration = this@SendTelegramAudioBuilder.duration
            performer = this@SendTelegramAudioBuilder.performer
            title = this@SendTelegramAudioBuilder.title
            disableNotification = this@SendTelegramAudioBuilder.disableNotification
            replyToMessageId = this@SendTelegramAudioBuilder.replyToMessageId
            allowSendingWithoutReply = this@SendTelegramAudioBuilder.allowSendingWithoutReply
            replyMarkup = this@SendTelegramAudioBuilder.inlineKeyboard.toReplyMarkup()
        }
}
class AudioBuilder : TelegramInputFileBuilder()
