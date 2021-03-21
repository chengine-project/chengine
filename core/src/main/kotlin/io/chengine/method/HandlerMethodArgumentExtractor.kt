package io.chengine.method

import io.chengine.connector.BotRequestContext

interface HandlerMethodArgumentExtractor {

    fun extractArgumentsFrom(request: BotRequestContext, method: HandlerMethod): Array<Any?>

}