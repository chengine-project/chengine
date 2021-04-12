package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage

fun pinTelegramChatMessage(block: PinTelegramChatMessageBuilder.() -> Unit) =
    PinTelegramChatMessageBuilder().apply(block)._build()

class PinTelegramChatMessageBuilder {

    var chatId: Long? = null
    var messageId: Int? = null
    var disableNotification: Boolean? = null

    fun _build() = PinChatMessage().apply {
        this@PinTelegramChatMessageBuilder.chatId?.let {
            this.chatId = it.toString()
        }
        this@PinTelegramChatMessageBuilder.messageId?.let {
            this.messageId = it
        }
        this@PinTelegramChatMessageBuilder.disableNotification?.let {
            this.disableNotification = it
        }
    }
}