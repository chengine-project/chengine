package io.chengine.interceptor

import io.chengine.connector.BotRequestContext

interface RequestInterceptorChain {

    fun doIntercept(request: BotRequestContext)

}