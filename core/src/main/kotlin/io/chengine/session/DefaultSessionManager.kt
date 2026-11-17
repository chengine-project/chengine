package io.chengine.session

import com.github.benmanes.caffeine.cache.Ticker
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
open class DefaultSessionManager @Autowired constructor() : SessionManager, SessionCacheManager {
    private var store: SessionStore = InMemorySessionStore()
    internal constructor(ticker: Ticker) : this() { store = InMemorySessionStore(ticker) }
    /** Call during initialization, before processing requests. */
    fun useStore(sessionStore: SessionStore) { store = sessionStore }

    override fun create(expirationTime: Int, timeDurationUnit: TimeUnit) {
        require(expirationTime > 0) { "Session expiration must be positive" }
    }
    override fun getSessionBy(sessionKey: SessionKey): Session? = store.load(sessionKey)
    override fun putSessionBy(sessionKey: SessionKey, session: Session): Session {
        require(session.sessionKey() == sessionKey) { "Session key does not match the requested key" }
        require(session.expirationTime().first > 0) { "Session expiration must be positive" }
        store.save(sessionKey, session)
        return session
    }
    override fun invalidateSessionBy(sessionKey: SessionKey) = store.delete(sessionKey)
}
