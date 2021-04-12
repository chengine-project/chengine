package io.chengine.interceptor

import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class DefaultRequestInterceptorChainFactory(
    private val interceptors: List<RequestInterceptor>
) : RequestInterceptorChainFactory {

    private val logger = logger()

    override fun get(): RequestInterceptorChain {
        logger.debug {
            val interceptorNames = interceptors
                .map { interceptor -> interceptor::class.simpleName }
                .joinToString(",")
            "Request interceptor chain initializing. Interceptors: $interceptorNames"
        }
        return DefaultInterceptorChain(interceptors)
    }
}