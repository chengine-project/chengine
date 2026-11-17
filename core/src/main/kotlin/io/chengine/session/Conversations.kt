package io.chengine.session

import io.chengine.connector.BotRequestContext
import java.util.concurrent.TimeUnit

/** Conversation state is available while a request handler is running. */
object Conversations {
    fun start(request: BotRequestContext, step: String, timeout: Int = 10, unit: TimeUnit = TimeUnit.MINUTES): ConversationSession {
        val key = requireNotNull(request.sessionKey()) { "This request has no user and chat session key" }
        val session = ConversationSession(key, step, timeout, unit)
        UserSessionContextHolder.start(session)
        return session
    }

    fun current(): ConversationSession? = UserSessionContextHolder.currentSession() as? ConversationSession

    fun end() = UserSessionContextHolder.end()
}
