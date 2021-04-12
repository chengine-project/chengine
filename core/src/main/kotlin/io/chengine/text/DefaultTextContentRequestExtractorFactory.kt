package io.chengine.text

import io.chengine.connector.Bot
import io.chengine.connector.DefaultRequestExtractorFactory
import org.springframework.stereotype.Component

@Component
class DefaultTextContentRequestExtractorFactory : DefaultRequestExtractorFactory<TextContentRequestExtractor>() {

    override fun put(bot: Bot) {
        commandExtractorMap[bot.botApiIdentifier().identifier()] = bot.textContentExtractor()
    }
}