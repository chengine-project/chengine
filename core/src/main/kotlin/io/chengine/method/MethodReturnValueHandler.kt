package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import kotlin.reflect.KClass

interface MethodReturnValueHandler<T : Any> {

    fun support(): KClass<out T>

    fun setNext(methodReturnValueHandler: MethodReturnValueHandler<Any>)

    fun handle(
        returnedObject: Any,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    )

}