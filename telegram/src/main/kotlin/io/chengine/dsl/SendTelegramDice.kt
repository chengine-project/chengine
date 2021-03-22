package io.chengine.dsl

import io.chengine.common.isNull
import org.telegram.telegrambots.meta.api.methods.send.SendDice

fun sendTelegramDice(block: SendTelegramDiceBuilder.() -> Unit): SendDice {
    return SendTelegramDiceBuilder().apply(block)._build()
}

enum class DiceEmoji(val code: String) {
    DICE("🎲"),
    TARGET("🎯"),
    BALL("🏀")
}

class SendTelegramDiceBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
): InlineKeyboardSupporter {
    var charId: String? = null
    var emoji: String? = null
    var allowSendingWithoutReply: Boolean? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: Int? = null
    fun _build(): SendDice {
        emoji.isNull {
            throw RuntimeException("Emoji can't be null or empty")
        }
        val sendDice = SendDice()
        charId?.let {
            sendDice.chatId = it
        }
        sendDice.emoji = emoji!!
        sendDice.allowSendingWithoutReply = allowSendingWithoutReply
        sendDice.disableNotification = disableNotification
        sendDice.replyToMessageId = replyToMessageId

        return sendDice
    }
}










