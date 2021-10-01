package io.chengine.handler

import io.chengine.connector.BotApiIdentifier
import io.chengine.connector.BotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.connector.get
import org.telegram.telegrambots.meta.api.objects.Update
import kotlin.reflect.KClass

class TelegramSingleHandlerAnnotationExtractor : SingleHandlerAnnotationExtractor {

    override fun support(): BotApiIdentifier {
        return TelegramBotApiIdentifier.instance
    }

    override fun extractFrom(request: BotRequestContext): KClass<out Annotation>? {
        return request.get<Update>()?.let {
            it.inlineQuery?.let {
                TelegramHandleInlineQuery::class
            } ?: it.poll?.let {
                TelegramHandlePoll::class
            } ?: it.pollAnswer?.let {
                TelegramHandlePollAnswer::class
            } ?: it.preCheckoutQuery?.let {
                TelegramHandlePayment::class
            } ?: it.message?.let { m ->
                m.location?.let {
                    TelegramHandleLocation::class
                } ?: m.poll?.let {
                    TelegramHandlePoll::class
                } ?: m.contact?.let {
                    TelegramHandleContact::class
                } ?: m.text?.let { text ->
                    return if (!text.startsWith("/")) {
                        TelegramHandleText::class
                    } else {
                        null
                    }
                }
            }
        }
    }
}