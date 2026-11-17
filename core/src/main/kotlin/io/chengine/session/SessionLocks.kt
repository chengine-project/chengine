package io.chengine.session

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/** Serializes a key across Chengine instances in this JVM; entries are removed after use. */
internal object SessionLocks {
    private class Entry { val lock = ReentrantLock(true); var users = 0 }
    private val entries = ConcurrentHashMap<SessionKey, Entry>()

    fun <T> withSession(key: SessionKey, block: () -> T): T {
        val entry = entries.compute(key) { _, current ->
            (current ?: Entry()).also { it.users++ }
        }!!
        try {
            return entry.lock.withLock(block)
        } finally {
            entries.compute(key) { _, current ->
                check(current === entry)
                entry.users--
                if (entry.users == 0) null else entry
            }
        }
    }
}
