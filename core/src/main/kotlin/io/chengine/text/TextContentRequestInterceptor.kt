package io.chengine.text

import io.chengine.connector.BotRequestContext
import io.chengine.connector.DefaultBotRequestContext
import io.chengine.interceptor.RequestInterceptor
import io.chengine.interceptor.RequestInterceptorChain
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class TextContentRequestInterceptor(
    private val textContentContentRequestExtractorFactory: DefaultTextContentRequestExtractorFactory
) : RequestInterceptor {

    private val logger = logger()

    override fun doIntercept(request: BotRequestContext, chain: RequestInterceptorChain) {
        textContentContentRequestExtractorFactory.get(request.apiIdentifier())?.let { requestExtractor ->
            val defaultBotRequestContext = request as DefaultBotRequestContext
            val textContent = requestExtractor.extractFrom(request)
            defaultBotRequestContext.textContent = textContent
            logger.debug { "Request intercepted. Command is `${textContent}`" }
        }
        chain.doIntercept(request)
    }
}