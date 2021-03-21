package io.chengine.handler

import kotlin.reflect.KClass

class TelegramSingleHandlerAnnotationProcessor : AbstractSingleHandlerAnnotationProcessor() {
    override fun support(): Set<KClass<out Annotation>> {
        return setOf(
            TelegramHandleChosenInlineQuery::class,
            TelegramHandleInlineQuery::class,
            TelegramHandlePollAnswer::class,
            TelegramHandleLocation::class,
            TelegramHandlePayment::class,
            TelegramHandleContact::class,
            TelegramHandleMedia::class,
            TelegramHandleText::class,
            TelegramHandlePoll::class,
        )
    }
}