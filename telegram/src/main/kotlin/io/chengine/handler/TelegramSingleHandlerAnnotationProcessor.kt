package io.chengine.handler

import io.chengine.handler.payment.TelegramHandlePreCheckout
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class TelegramSingleHandlerAnnotationProcessor(handlerRegistry: DefaultHandlerRegistry) :
    AbstractSingleHandlerAnnotationProcessor(handlerRegistry) {
    override fun support(): Set<KClass<out Annotation>> {
        return setOf(
            TelegramHandleChosenInlineQuery::class,
            TelegramHandleInlineQuery::class,
            TelegramHandlePollAnswer::class,
            TelegramHandleLocation::class,
            TelegramHandlePreCheckout::class,
            TelegramHandleContact::class,
            TelegramHandleMedia::class,
            TelegramHandleText::class,
            TelegramHandlePoll::class,
        )
    }
}