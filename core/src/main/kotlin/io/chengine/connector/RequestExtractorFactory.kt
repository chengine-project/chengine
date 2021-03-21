package io.chengine.connector

interface RequestExtractorFactory<T : RequestExtractor<*>> : Factory {

    fun get(apiIdentifier: BotApiIdentifier): T?

}