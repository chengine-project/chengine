package io.chengine.session

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.Expiry
import com.github.benmanes.caffeine.cache.Ticker

/** Sessions expire after their configured period of inactivity. */
class InMemorySessionStore internal constructor(ticker: Ticker) : SessionStore {
    constructor() : this(Ticker.systemTicker())

    private val sessions: Cache<SessionKey, Session> = Caffeine.newBuilder()
        .ticker(ticker)
        .expireAfter(object : Expiry<SessionKey, Session> {
            private fun duration(session: Session): Long {
                val (amount, unit) = session.expirationTime()
                return unit.toNanos(amount.toLong())
            }
            override fun expireAfterCreate(key: SessionKey, value: Session, currentTime: Long): Long = duration(value)
            override fun expireAfterUpdate(key: SessionKey, value: Session, currentTime: Long, currentDuration: Long): Long = duration(value)
            override fun expireAfterRead(key: SessionKey, value: Session, currentTime: Long, currentDuration: Long): Long = duration(value)
        }).build()

    override fun load(key: SessionKey): Session? = sessions.getIfPresent(key)
    override fun save(key: SessionKey, session: Session) { sessions.put(key, session) }
    override fun delete(key: SessionKey) = sessions.invalidate(key)
}
