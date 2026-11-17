package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import kotlin.reflect.KClass

/** Converts a handler's text response into a Telegram message addressed to the request chat. */
class StringReturnValueHandler : AbstractMethodReturnValueHandler<String>() {
    override fun support(): KClass<String> = String::class

    override fun process(returnedObject: Any, handlerMethod: HandlerMethod, requestContext: BotRequestContext, responseContext: BotResponseContext) {
        val message = SendMessage.builder().chatId("").text(returnedObject as String).build()
        PartialApiMethodReturnValueHandler().process(message, handlerMethod, requestContext, responseContext)
    }
}
