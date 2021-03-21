package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext

abstract class AbstractMethodReturnValueHandler<T : Any> : MethodReturnValueHandler<T> {

    private var nextHandler: MethodReturnValueHandler<Any>? = null

    abstract fun process(
        returnedObject: Any,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    )

    override fun handle(
        returnedObject: Any,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    ) {
        process(returnedObject, handlerMethod, requestContext, responseContext)
        nextHandler?.let {
            responseContext.currentResponseObject()?.let {
                nextHandler!!.handle(it, handlerMethod, requestContext, responseContext)
            }
        }
    }

    override fun setNext(methodReturnValueHandler: MethodReturnValueHandler<Any>) {
        this.nextHandler = methodReturnValueHandler
    }
}