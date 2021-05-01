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
import io.chengine.session.SessionKeyRequestExtractor
import io.chengine.session.TelegramSessionKeyRequestExtractor
import io.chengine.text.TelegramTextContentRequestExtractor
import io.chengine.text.TextContentRequestExtractor
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendAudio
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto

open class ChengineTelegramLongPoolingBot : AbstractBot() {

    lateinit var telegramBotsLongPollingBotBot: TelegramBotsLongPoolingBot

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

    override fun commandRequestExtractor(): CommandRequestExtractor = TelegramCommandRequestExtractor()

    override fun singleHandlerAnnotationExtractor(): SingleHandlerAnnotationExtractor = TelegramSingleHandlerAnnotationExtractor()

    override fun methodReturnValueHandlers(): List<MethodReturnValueHandler<*>> = listOf(
        PartialApiMethodReturnValueHandler()
    )

    override fun commandExtractor(): CommandRequestExtractor = TelegramCommandRequestExtractor()

    override fun textContentExtractor(): TextContentRequestExtractor = TelegramTextContentRequestExtractor()

    override fun singleHandlerAnnotationProcessor(): List<AbstractSingleHandlerAnnotationProcessor> = listOf(
        TelegramSingleHandlerAnnotationProcessor()
    )

    private fun executeMethod(method: Any) {
        when (method) {
            is BotApiMethod<*> -> telegramBotsLongPollingBotBot.execute(method)
            is SendMediaGroup -> telegramBotsLongPollingBotBot.execute(method)
            is SendPhoto -> telegramBotsLongPollingBotBot.execute(method)
            is SendPoll -> telegramBotsLongPollingBotBot.execute(method)
            is SendAudio -> telegramBotsLongPollingBotBot.execute(method)
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