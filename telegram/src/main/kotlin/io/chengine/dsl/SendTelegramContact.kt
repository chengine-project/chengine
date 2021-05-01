package io.chengine.dsl

import io.chengine.common.isNull
import org.telegram.telegrambots.meta.api.methods.send.SendContact

fun sendTelegramContact(block: SendTelegramContactBuilder.() -> Unit): SendContact {
    return SendTelegramContactBuilder().apply(block)._build()
}

class SendTelegramContactBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {

    var chatId: String? = null
    var allowSendingWithoutReply: Boolean? = null
    var disableNotification: Boolean? = null
    var firstName: String? = null
    var lastName: String? = null
    var phoneNumber: String? = null
    var replyToMessageId: Int? = null
    var vCard: String? = null

    fun _build(): SendContact {

        val sendContact = SendContact()
        chatId?.let {
            sendContact.chatId = it
        }
        sendContact.allowSendingWithoutReply = allowSendingWithoutReply
        sendContact.disableNotification = disableNotification
        sendContact.firstName = firstName ?: throw RuntimeException("Contact first name can't be null")
        sendContact.lastName = lastName
        sendContact.phoneNumber = phoneNumber ?: throw RuntimeException("Phone number can't be null")
        sendContact.replyToMessageId = replyToMessageId
        sendContact.vCard = vCard
        sendContact.replyMarkup = inlineKeyboard.toReplyMarkup()

        return sendContact
    }
}