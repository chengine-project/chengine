package io.chengine.session

import com.github.benmanes.caffeine.cache.Ticker
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong

class DefaultSessionManagerTest {
    @Test fun `expires after inactivity and access renews timeout`() {
        val nanos = AtomicLong()
        val manager = DefaultSessionManager(Ticker { nanos.get() })
        val key = SessionKey("1", "2", "telegram")
        val session = ConversationSession(key, "name", 10, TimeUnit.SECONDS)
        manager.putSessionBy(key, session)
        nanos.addAndGet(TimeUnit.SECONDS.toNanos(9))
        assertSame(session, manager.getSessionBy(key))
        nanos.addAndGet(TimeUnit.SECONDS.toNanos(9))
        assertSame(session, manager.getSessionBy(key))
        nanos.addAndGet(TimeUnit.SECONDS.toNanos(11))
        assertNull(manager.getSessionBy(key))
    }
    @Test fun `validates key and timeout`() {
        val manager = DefaultSessionManager()
        val key = SessionKey("1", "2", "telegram")
        assertThrows(IllegalArgumentException::class.java) { manager.putSessionBy(key, ConversationSession(key, "name", 0)) }
        assertThrows(IllegalArgumentException::class.java) { manager.putSessionBy(key, ConversationSession(SessionKey("other", "2", "telegram"), "name")) }
    }
}
