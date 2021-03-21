package io.chengine.interceptor

import io.chengine.connector.BotRequestContext

class DefaultInterceptorChain(interceptors: List<RequestInterceptor>) : RequestInterceptorChain {

    private var iterator: Iterator<RequestInterceptor> = interceptors.iterator()

    override fun doIntercept(request: BotRequestContext) {
        if (iterator.hasNext()) {
            iterator.next().doIntercept(request, this)
        }
    }
}