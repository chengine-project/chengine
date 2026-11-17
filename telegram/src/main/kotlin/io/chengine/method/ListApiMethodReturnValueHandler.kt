package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import io.chengine.connector.DefaultBotResponseContext
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod
import kotlin.reflect.KClass

/** Prepares each response just like an individual Telegram API method. */
class ListApiMethodReturnValueHandler : AbstractMethodReturnValueHandler<Collection<*>>() {
    override fun support(): KClass<out Collection<*>> = Collection::class

    override fun process(
        returnedObject: Any,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    ) {
        val methods = (returnedObject as Collection<*>).toList()
        methods.forEachIndexed { index, method ->
            require(method is PartialBotApiMethod<*>) {
                "Response at index $index must be a Telegram API method"
            }
        }
        val singleHandler = PartialApiMethodReturnValueHandler()
        val prepared = methods.map { method ->
            val individual = DefaultBotResponseContext()
            singleHandler.process(method!!, handlerMethod, requestContext, individual)
            checkNotNull(individual.currentResponseObject())
        }
        (responseContext as DefaultBotResponseContext).currentResponseObject = prepared
    }
}
