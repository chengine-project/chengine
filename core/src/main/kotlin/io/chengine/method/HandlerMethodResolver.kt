package io.chengine.method

import io.chengine.connector.BotRequestContext

interface HandlerMethodResolver {

    fun resolve(request: BotRequestContext): HandlerMethod?

}