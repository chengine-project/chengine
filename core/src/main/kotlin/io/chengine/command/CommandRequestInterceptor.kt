package io.chengine.command

import io.chengine.connector.BotRequestContext
import io.chengine.connector.DefaultBotRequestContext
import io.chengine.connector.RequestExtractorFactory
import io.chengine.interceptor.RequestInterceptor
import io.chengine.interceptor.RequestInterceptorChain
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class CommandRequestInterceptor(private val commandRequestExtractorFactory: RequestExtractorFactory<CommandRequestExtractor>) : RequestInterceptor {

    private val logger = logger()

    override fun doIntercept(request: BotRequestContext, chain: RequestInterceptorChain) {
        val apiIdentifier = request.apiIdentifier()
        val extractor = commandRequestExtractorFactory.get(apiIdentifier)
        val defaultContext = request as DefaultBotRequestContext
        defaultContext.command = extractor?.extractFrom(request)
        logger.debug { "Request intercepted. Command is `${defaultContext.command}`" }

        chain.doIntercept(request)
    }
}