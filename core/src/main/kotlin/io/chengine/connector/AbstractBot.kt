package io.chengine.connector

import io.chengine.message.MessageProcessor
import io.chengine.session.SessionLocks
import java.util.UUID

abstract class AbstractBot : Bot {

    private val instanceId = UUID.randomUUID().toString()
    /** Override with a stable, non-secret bot account identifier when available. */
    open fun sessionBotIdentifier(): String = instanceId

    private lateinit var messageProcessor: MessageProcessor<BotRequestContext, BotResponseContext>

    fun process(request: Any) {
        val botRequest = DefaultBotRequestContext()
        botRequest.contextMap[request::class] = request
        botRequest.contextMap[Bot::class] = this
        botRequest.botApiIdentifier = botApiIdentifier()

        val key = sessionKeyExtractor().extractFrom(botRequest)?.copy(botId = sessionBotIdentifier())
        botRequest.sessionKey = key
        val handle = {
            val botResponse = DefaultBotResponseContext()
            messageProcessor.process(botRequest, botResponse)
            if (botResponse.currentResponseObject() != null) executeResponse(botResponse)
        }
        if (key == null) handle() else SessionLocks.withSession(key, handle)
    }

    override fun setMessageProcessor(messageProcessor: MessageProcessor<BotRequestContext, BotResponseContext>) {
        this.messageProcessor = messageProcessor
    }
}