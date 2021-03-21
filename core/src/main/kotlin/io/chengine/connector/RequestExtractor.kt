package io.chengine.connector

/**
 * Extracts [T] object from [BotRequestContext]
 */
interface RequestExtractor<T> {

    fun support(): BotApiIdentifier

    fun extractFrom(request: BotRequestContext): T?

}