package io.chengine.command

import io.chengine.connector.Bot
import io.chengine.connector.DefaultRequestExtractorFactory
import org.springframework.stereotype.Component

@Component
class DefaultCommandRequestExtractorFactory : DefaultRequestExtractorFactory<CommandRequestExtractor>() {

    override fun put(bot: Bot) {
        commandExtractorMap[bot.botApiIdentifier().identifier()] = bot.commandExtractor()
    }

}