package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote
import org.telegram.telegrambots.meta.api.objects.InputFile

fun sendTelegramVideoNote(block: SendTelegramVideoNoteBuilder.() -> Unit): SendVideoNote =
    SendTelegramVideoNoteBuilder().apply(block)._build()

class SendTelegramVideoNoteBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: String? = null
    var videoNote: InputFile? = null
    var duration: Int? = null
    var length: Int? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    var allowSendingWithoutReply: Boolean? = null
    fun videoNote(block: TelegramInputFileBuilder.() -> Unit) { videoNote = TelegramInputFileBuilder().apply(block)._build() }
    fun _build(): SendVideoNote = SendVideoNote.builder().chatId(chatId ?: "")
        .videoNote(requireNotNull(videoNote) { "Video note is required" }).build().apply {
            duration = this@SendTelegramVideoNoteBuilder.duration
            length = this@SendTelegramVideoNoteBuilder.length
            disableNotification = this@SendTelegramVideoNoteBuilder.disableNotification
            replyToMessageId = this@SendTelegramVideoNoteBuilder.replyToMessageId
            allowSendingWithoutReply = this@SendTelegramVideoNoteBuilder.allowSendingWithoutReply
            replyMarkup = inlineKeyboard.toReplyMarkup()
        }
}
