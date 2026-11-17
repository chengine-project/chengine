package io.chengine.agent

import io.chengine.connector.BotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.generics.TelegramClient

/** Experimental tool bound to the chat of a single Telegram request. */
class TelegramSendMessageTool(request: BotRequestContext, private val client: TelegramClient) {
    private val chatId: String

    init {
        require(request.apiIdentifier().identifier() == TelegramBotApiIdentifier.instance.identifier()) {
            "The send-message tool requires a Telegram request"
        }
        chatId = requireNotNull(request.sessionKey()?.chatId).also {
            require(it.isNotBlank()) { "The request chat ID must not be blank" }
        }
    }

    val name: String = "send_telegram_message"
    val description: String = "Send a plain-text message to the current Telegram chat."

    /** Returns the message ID after Telegram confirms the send; errors propagate to the caller. */
    fun execute(text: String): Int {
        require(text.isNotBlank()) { "Message text must not be blank" }
        val message = SendMessage.builder().chatId(chatId).text(text).build()
        return client.execute(message).messageId
    }
}
