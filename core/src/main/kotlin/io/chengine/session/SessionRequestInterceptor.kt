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
    private val sessionManager: SessionManager,
    private val sessionKeyExtractorFactory: RequestExtractorFactory<SessionKeyRequestExtractor>
) : RequestInterceptor {

    private val logger = logger()

    override fun doIntercept(request: BotRequestContext, chain: RequestInterceptorChain) {
        UserSessionContextHolder.setSession(null)
        val apiIdentifier = request.apiIdentifier()
        val ketExtractor = sessionKeyExtractorFactory.get(apiIdentifier)
        ketExtractor?.extractFrom(request)?.let {
            val defaultContext = request as DefaultBotRequestContext
            defaultContext.sessionKey = it
            UserSessionContextHolder.setSession(session = sessionManager.getSessionBy(it))
            logger.debug { "Request intercepted. Current session is: ${UserSessionContextHolder.currentSession()}" }
        } ?: run {
            logger.info { "Session wasn't set, because `${apiIdentifier.identifier()}` session extractor return null" }
        }

        chain.doIntercept(request)
    }
}