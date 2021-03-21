package io.chengine.connector

import io.chengine.message.MessageProcessor

interface BotRequestBotResponseMessageProcessorAware {

    fun setMessageProcessor(messageProcessor: MessageProcessor<BotRequestContext, BotResponseContext>)

}