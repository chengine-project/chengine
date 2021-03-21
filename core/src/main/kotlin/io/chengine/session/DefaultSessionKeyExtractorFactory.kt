package io.chengine.session

import io.chengine.connector.Bot
import io.chengine.connector.DefaultRequestExtractorFactory
import org.springframework.stereotype.Component

@Component
open class DefaultSessionKeyExtractorFactory : DefaultRequestExtractorFactory<SessionKeyRequestExtractor>()  {
    override fun put(bot: Bot) {
        commandExtractorMap[bot.botApiIdentifier().identifier()] = bot.sessionKeyExtractor()
    }
}