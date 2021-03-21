package io.chengine.interceptor

import io.chengine.connector.BotRequestContext

interface RequestInterceptor {

    fun doIntercept(request: BotRequestContext, chain: RequestInterceptorChain)

}