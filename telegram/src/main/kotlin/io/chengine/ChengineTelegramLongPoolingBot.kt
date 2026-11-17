package io.chengine

import io.chengine.command.CommandRequestExtractor
import io.chengine.command.TelegramCommandRequestExtractor
import io.chengine.connector.AbstractBot
import io.chengine.connector.BotApiIdentifier
import io.chengine.connector.BotResponseContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.handler.AbstractSingleHandlerAnnotationProcessor
import io.chengine.handler.SingleHandlerAnnotationExtractor
import io.chengine.handler.TelegramSingleHandlerAnnotationExtractor
import io.chengine.handler.TelegramSingleHandlerAnnotationProcessor
import io.chengine.method.MethodReturnValueHandler
import io.chengine.method.PartialApiMethodReturnValueHandler
import io.chengine.method.ListApiMethodReturnValueHandler
import io.chengine.poll.InMemoryTelegramPollOriginStore
import io.chengine.poll.TelegramPollOrigin
import io.chengine.poll.TelegramPollOriginStore
import io.chengine.session.SessionKeyRequestExtractor
import io.chengine.session.TelegramSessionKeyRequestExtractor
import io.chengine.text.TelegramTextContentRequestExtractor
import io.chengine.text.TextContentRequestExtractor
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendAudio
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendAnimation
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.methods.send.SendVideoNote
import org.telegram.telegrambots.meta.api.methods.send.SendSticker
import org.telegram.telegrambots.meta.api.methods.send.SendVoice
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto

open class ChengineTelegramLongPoolingBot : AbstractBot() {

    lateinit var telegramBotsLongPollingBotBot: TelegramBotsLongPoolingBot

    /** Configure before processing requests; share a durable implementation across instances if needed. */
    var pollOriginStore: TelegramPollOriginStore = InMemoryTelegramPollOriginStore()

    override fun sessionBotIdentifier(): String =
        if (::telegramBotsLongPollingBotBot.isInitialized)
            telegramBotsLongPollingBotBot.userName.removePrefix("@").lowercase(java.util.Locale.ROOT)
        else super.sessionBotIdentifier()

    override fun botApiIdentifier(): BotApiIdentifier = TelegramBotApiIdentifier.instance

    override fun executeResponse(response: BotResponseContext) {
        response.currentResponseObject()?.let {
            when (it) {
                // Not all methods could be process by
                is Collection<*> -> it.forEach { method ->
                    method?.let {
                        executeMethod(method)
                    }
                }
                else -> executeMethod(it)
            }
        }
    }

    override fun sessionKeyExtractor(): SessionKeyRequestExtractor = TelegramSessionKeyRequestExtractor()

    override fun commandRequestExtractor(): CommandRequestExtractor = TelegramCommandRequestExtractor { if (::telegramBotsLongPollingBotBot.isInitialized) telegramBotsLongPollingBotBot.userName else null }

    override fun singleHandlerAnnotationExtractor(): SingleHandlerAnnotationExtractor = TelegramSingleHandlerAnnotationExtractor()

    override fun methodReturnValueHandlers(): List<MethodReturnValueHandler<*>> = listOf(
        PartialApiMethodReturnValueHandler(),
        ListApiMethodReturnValueHandler()
    )

    override fun commandExtractor(): CommandRequestExtractor = TelegramCommandRequestExtractor { if (::telegramBotsLongPollingBotBot.isInitialized) telegramBotsLongPollingBotBot.userName else null }

    override fun textContentExtractor(): TextContentRequestExtractor = TelegramTextContentRequestExtractor()
    override fun singleHandlerAnnotationProcessor(): List<AbstractSingleHandlerAnnotationProcessor> = emptyList()

    private fun executeMethod(method: Any) {
        when (method) {
            is SendPoll -> {
                val sent = telegramBotsLongPollingBotBot.client.execute(method)
                val poll = requireNotNull(sent.poll) { "Telegram sent a poll without a poll ID" }
                val chatId = requireNotNull(sent.chat?.id) { "Telegram sent a poll without a chat ID" }
                val messageId = requireNotNull(sent.messageId) { "Telegram sent a poll without a message ID" }
                pollOriginStore.put(sessionBotIdentifier(), TelegramPollOrigin(poll.id, chatId, messageId))
            }
            is BotApiMethod<*> -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendMediaGroup -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendPhoto -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendAudio -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendDocument -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendAnimation -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendVideo -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendVideoNote -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendSticker -> telegramBotsLongPollingBotBot.client.execute(method)
            is SendVoice -> telegramBotsLongPollingBotBot.client.execute(method)
            is EditMessageMedia -> telegramBotsLongPollingBotBot.client.execute(method)
            is SetChatPhoto -> telegramBotsLongPollingBotBot.client.execute(method)
            else -> throw RuntimeException(
                """
                       Response object is not assignable from BotApiMethod class. 
                       This is may a bug, please contact support mikheev.show@gmail.com 
                       also attach steps to reproduce the problem
                    """
            )
        }
    }
}