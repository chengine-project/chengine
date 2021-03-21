package io.chengine.connector

import io.chengine.command.CommandRequestExtractor
import io.chengine.handler.AbstractSingleHandlerAnnotationProcessor
import io.chengine.handler.SingleHandlerAnnotationExtractor
import io.chengine.method.MethodReturnValueHandler
import io.chengine.session.SessionKeyRequestExtractor

interface Bot : BotRequestBotResponseMessageProcessorAware {

    fun botApiIdentifier(): BotApiIdentifier

    fun sessionKeyExtractor(): SessionKeyRequestExtractor

    fun commandRequestExtractor(): CommandRequestExtractor

    fun singleHandlerAnnotationExtractor(): SingleHandlerAnnotationExtractor

    fun methodReturnValueHandlers(): List<MethodReturnValueHandler<*>>

    fun commandExtractor(): CommandRequestExtractor

    fun singleHandlerAnnotationProcessor(): List<AbstractSingleHandlerAnnotationProcessor>

    fun executeResponse(response: BotResponseContext)

}