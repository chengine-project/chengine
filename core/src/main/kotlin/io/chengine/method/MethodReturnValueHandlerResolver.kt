package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext

interface MethodReturnValueHandlerResolver {

    fun resolve(
        returnedObject: Any?,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    )

}