package io.chengine.session

import io.chengine.connector.BotRequestContext
import io.chengine.connector.DefaultBotRequestContext
import io.chengine.connector.RequestExtractorFactory
import io.chengine.interceptor.RequestInterceptor
import io.chengine.interceptor.RequestInterceptorChain
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component

@Component
open class SessionRequestInterceptor(
    private val sessionManager: SessionManager
) : RequestInterceptor {

    private val logger = logger()

    override fun doIntercept(request: BotRequestContext, chain: RequestInterceptorChain) {
        UserSessionContextHolder.setSession(null)
        UserSessionContextHolder.bind(sessionManager)
        val apiIdentifier = request.apiIdentifier()
        request.sessionKey()?.let {
            val defaultContext = request as DefaultBotRequestContext
            defaultContext.sessionKey = it
            UserSessionContextHolder.setSession(session = sessionManager.getSessionBy(it))
            logger.debug { "Request intercepted. Current session is: ${UserSessionContextHolder.currentSession()}" }
        } ?: run {
            logger.info { "Session wasn't set for `${apiIdentifier.identifier()}`" }
        }

        chain.doIntercept(request)
    }
}