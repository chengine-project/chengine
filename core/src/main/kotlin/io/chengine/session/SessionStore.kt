package io.chengine.session

/**
 * Storage for sessions. load must renew the inactivity timeout; save replaces the state and
 * renews that timeout. Implementations must support concurrent access for different keys.
 * Chengine serializes requests per key only within a single JVM, not across processes.
 */
interface SessionStore {
    fun load(key: SessionKey): Session?
    fun save(key: SessionKey, session: Session)
    fun delete(key: SessionKey)
}
