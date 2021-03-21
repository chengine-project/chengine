package io.chengine.command

import io.chengine.connector.Bot
import io.chengine.connector.BotApiIdentifier

interface CommandRequestExtractorFactory {

    fun put(botApiIdentifier: BotApiIdentifier, commandExtractor: CommandRequestExtractor)

    fun get(botApiIdentifier: BotApiIdentifier): CommandRequestExtractor

}