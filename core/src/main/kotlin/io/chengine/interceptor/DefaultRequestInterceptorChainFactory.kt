package io.chengine.interceptor

import org.springframework.stereotype.Component

@Component
class DefaultRequestInterceptorChainFactory(private val interceptors: List<RequestInterceptor>) :
    RequestInterceptorChainFactory {

    override fun get(): RequestInterceptorChain {
        return DefaultInterceptorChain(interceptors)
    }
}