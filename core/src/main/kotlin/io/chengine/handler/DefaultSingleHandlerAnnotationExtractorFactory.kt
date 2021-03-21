package io.chengine.handler

import io.chengine.connector.Bot
import io.chengine.connector.DefaultRequestExtractorFactory
import org.springframework.stereotype.Component

@Component
open class DefaultSingleHandlerAnnotationExtractorFactory : DefaultRequestExtractorFactory<SingleHandlerAnnotationExtractor>() {
    override fun put(bot: Bot) {
        commandExtractorMap[bot.botApiIdentifier().identifier()] = bot.singleHandlerAnnotationExtractor()
    }
}