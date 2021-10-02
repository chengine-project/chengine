package io.chengine.message

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import io.chengine.interceptor.RequestInterceptorChainFactory
import io.chengine.method.HandlerMethodArgumentExtractor
import io.chengine.method.HandlerMethodResolver
import io.chengine.method.MethodReturnValueHandlerResolver
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class DefaultMessageProcessor(
    private val interceptorChainFactory: RequestInterceptorChainFactory,
    private val handlerMethodResolver: HandlerMethodResolver,
    private val handlerMethodArgumentExtractor: HandlerMethodArgumentExtractor,
    private val methodReturnValueHandlerResolver: MethodReturnValueHandlerResolver
) : MessageProcessor<BotRequestContext, BotResponseContext> {

    val logger = logger()

    override fun process(request: BotRequestContext, response: BotResponseContext) {

        logger.debug { "Request received: $request" }

        interceptorChainFactory.get().doIntercept(request)
        handlerMethodResolver.resolve(request)?.let { method ->
            val args = handlerMethodArgumentExtractor.extractArgumentsFrom(request, method)
            method.invoke(Any::class, args)?.let { result ->
                methodReturnValueHandlerResolver.resolve(result, method, request, response)
            } ?: throw RuntimeException("Returned value is null, break message processing")
        } ?: throw RuntimeException("Can't resolve method to process the request: $request")
    }
}