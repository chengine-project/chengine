package io.chengine.interceptor

import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
class DefaultRequestInterceptorChainFactory(private val interceptors: List<RequestInterceptor>) :
    RequestInterceptorChainFactory {

    private val logger = logger()

    override fun get(): RequestInterceptorChain {
        logger.info { interceptors }
        return DefaultInterceptorChain(interceptors)
    }
}