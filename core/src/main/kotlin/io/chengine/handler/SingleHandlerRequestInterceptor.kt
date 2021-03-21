package io.chengine.handler

import io.chengine.connector.BotRequestContext
import io.chengine.connector.DefaultBotRequestContext
import io.chengine.connector.RequestExtractorFactory
import io.chengine.interceptor.RequestInterceptor
import io.chengine.interceptor.RequestInterceptorChain
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class SingleHandlerRequestInterceptor(private val requestExtractorFactory: RequestExtractorFactory<SingleHandlerAnnotationExtractor>) : RequestInterceptor {

    private val logger = logger()

    override fun doIntercept(request: BotRequestContext, chain: RequestInterceptorChain) {
        val botApiIdentifier = request.apiIdentifier()
        val extractor = requestExtractorFactory.get(botApiIdentifier)
        val defaultContext = request as DefaultBotRequestContext
        defaultContext.singleHandlerAnnotation = extractor?.extractFrom(request)
        logger.debug { "Request intercepted. Single handler annotation `${defaultContext.singleHandlerAnnotation}`" }

        chain.doIntercept(request)
    }
}