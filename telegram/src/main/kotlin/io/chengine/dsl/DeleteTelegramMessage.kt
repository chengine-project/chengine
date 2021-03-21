package io.chengine.dsl

import io.chengine.common.isNonNullButBlankOrEmpty
import io.chengine.common.isNull
import io.chengine.common.then
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage

fun deleteTelegramMessage(block: DeleteTelegramMessageBuilder.() -> Unit): DeleteMessage {
    return DeleteTelegramMessageBuilder().apply(block)._build()
}

class DeleteTelegramMessageBuilder {

    var chatId: String? = null
    var messageId: Int? = null

    fun _build(): DeleteMessage {
        chatId.isNonNullButBlankOrEmpty().then {
            throw RuntimeException("Chat identifier can't be blank or empty")
        }
        messageId.isNull {
            throw RuntimeException("Message id can't be null")
        }

        val deleteMessage = DeleteMessage()
        chatId?.let {
            deleteMessage.chatId = it
        }
        deleteMessage.messageId = messageId!!

        return deleteMessage
    }
}