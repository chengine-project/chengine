package io.chengine.handler

import io.chengine.connector.BotApiIdentifier

interface SingleHandlerAnnotationExtractorFactory {

    fun get(botApiIdentifier: BotApiIdentifier): SingleHandlerAnnotationExtractor

}