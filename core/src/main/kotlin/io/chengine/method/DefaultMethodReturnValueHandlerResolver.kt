package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import org.springframework.stereotype.Component

@Component
class DefaultMethodReturnValueHandlerResolver(
    private val methodReturnValueHandlerFactory: MethodReturnValueHandlerFactory
) : MethodReturnValueHandlerResolver {

    override fun resolve(
        returnedObject: Any?,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    ) {
        returnedObject?.let {
            methodReturnValueHandlerFactory.get(it::class)?.handle(
                returnedObject = it,
                handlerMethod = handlerMethod,
                requestContext = requestContext,
                responseContext = responseContext
            )
        }
    }
}