package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod
import java.io.Serializable
import kotlin.reflect.KClass

class ListApiMethodReturnValueHandler :
    AbstractMethodReturnValueHandler<Collection<PartialBotApiMethod<out Serializable>>>() {

    override fun process(
        returnedObject: Any,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    ) {
        TODO("Not yet implemented")
    }

    override fun support(): KClass<out Collection<PartialBotApiMethod<out Serializable>>> = TODO()
}