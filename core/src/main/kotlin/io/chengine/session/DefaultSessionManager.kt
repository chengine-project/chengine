package io.chengine.session

import com.github.benmanes.caffeine.cache.Cache
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@Component
open class DefaultSessionManager: SessionManager, SessionCacheManager {

    private val timeCacheHashMapReference = AtomicReference<Map<Int, Cache<SessionKey, Session>>>()

    override fun create(expirationTime: Int, timeDurationUnit: TimeUnit) {
        TODO("Not yet implemented")
    }

    override fun getSessionBy(sessionKey: SessionKey): Session? {
        return null
    }

    override fun putSessionBy(sessionKey: SessionKey, session: Session): Session {
        TODO("Not yet implemented")
    }

    override fun invalidateSessionBy(sessionKey: SessionKey) {
        TODO("Not yet implemented")
    }
}