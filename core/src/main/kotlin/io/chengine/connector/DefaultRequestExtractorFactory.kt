package io.chengine.connector

abstract class DefaultRequestExtractorFactory<T : RequestExtractor<*>> : RequestExtractorFactory<T> {

    protected val commandExtractorMap = HashMap<String, T>()

    override fun get(apiIdentifier: BotApiIdentifier): T? = commandExtractorMap[apiIdentifier.identifier()]

}