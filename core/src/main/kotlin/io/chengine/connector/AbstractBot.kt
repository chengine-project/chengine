package io.chengine.connector

import io.chengine.message.MessageProcessor

abstract class AbstractBot : Bot {

    private lateinit var messageProcessor: MessageProcessor<BotRequestContext, BotResponseContext>

    fun process(request: Any) {
        val botRequest = DefaultBotRequestContext()
        botRequest.contextMap[request::class] = request
        botRequest.botApiIdentifier = botApiIdentifier()

        val botResponse = DefaultBotResponseContext()

        messageProcessor.process(botRequest, botResponse)

        executeResponse(botResponse)
    }

    override fun setMessageProcessor(messageProcessor: MessageProcessor<BotRequestContext, BotResponseContext>) {
        this.messageProcessor = messageProcessor
    }
}