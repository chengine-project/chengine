package io.chengine.session

import io.chengine.connector.BotApiIdentifier

interface SessionKeyExtractorFactory {

    fun getSessionKeyExtractor(botApiIdentifier: BotApiIdentifier): SessionKeyRequestExtractor

}