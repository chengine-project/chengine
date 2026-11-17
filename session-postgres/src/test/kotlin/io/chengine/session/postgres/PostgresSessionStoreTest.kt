package io.chengine.session.postgres

import io.chengine.session.ConversationSession
import io.chengine.session.SessionKey
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assumptions.assumeTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import java.util.concurrent.TimeUnit

class PostgresSessionStoreTest {
    private lateinit var dataSource: PGSimpleDataSource
    private lateinit var store: PostgresSessionStore
    private val key = SessionKey("user", "chat", "telegram", "bot")

    @BeforeEach fun setup() {
        val url = System.getenv("CHENGINE_TEST_POSTGRES_URL")
        assumeTrue(!url.isNullOrBlank(), "Set CHENGINE_TEST_POSTGRES_URL to run PostgreSQL integration tests")
        dataSource = PGSimpleDataSource().apply {
            setURL(url)
            user = System.getenv("CHENGINE_TEST_POSTGRES_USER") ?: "postgres"
            password = System.getenv("CHENGINE_TEST_POSTGRES_PASSWORD") ?: ""
        }
        store = PostgresSessionStore(dataSource)
        store.initializeSchema()
        clear()
    }
    @AfterEach fun teardown() { if (::dataSource.isInitialized) clear() }
    private fun clear() { dataSource.connection.use { c -> c.createStatement().use { it.executeUpdate("DELETE FROM chengine_sessions") } } }

    @Test fun `restores state in new store and isolates bots users chats and null keys`() {
        val data = mapOf("name" to "Аня", "colon:=\\\n" to "строка\nsecond=☺")
        store.save(key, ConversationSession(key, "city", 3, TimeUnit.MINUTES, data))
        val otherBot = key.copy(botId = "other")
        val otherUser = key.copy(userId = "other")
        val otherChat = key.copy(chatId = "other")
        val nullUser = key.copy(userId = null)
        val emptyUser = key.copy(userId = "")
        listOf(otherBot, otherUser, otherChat, nullUser, emptyUser).forEachIndexed { i, k ->
            store.save(k, ConversationSession(k, "step$i"))
        }
        val restored = PostgresSessionStore(dataSource)
        val session = restored.load(key)!!
        assertEquals("city", session.step)
        assertEquals(data, session.values())
        assertEquals(3 to TimeUnit.MINUTES, session.expirationTime())
        listOf(otherBot, otherUser, otherChat, nullUser, emptyUser).forEachIndexed { i, k -> assertEquals("step$i", restored.load(k)!!.step) }
        restored.delete(otherBot)
        assertNull(store.load(otherBot))
        assertNotNull(store.load(key))
    }

    @Test fun `load renews timeout and expired sessions are not resurrected`() {
        store.save(key, ConversationSession(key, "name", 60, TimeUnit.SECONDS))
        dataSource.connection.use { c -> c.createStatement().use { it.executeUpdate("UPDATE chengine_sessions SET expires_at = clock_timestamp() + interval '1 second'") } }
        assertNotNull(store.load(key))
        dataSource.connection.use { c -> c.createStatement().use { s -> s.executeQuery("SELECT expires_at > clock_timestamp() + interval '30 seconds' FROM chengine_sessions").use { r -> r.next(); assertTrue(r.getBoolean(1)) } } }
        dataSource.connection.use { c -> c.createStatement().use { it.executeUpdate("UPDATE chengine_sessions SET expires_at = clock_timestamp() - interval '1 second'") } }
        assertNull(store.load(key))
        assertEquals(1, store.deleteExpired())
        assertEquals(0, store.deleteExpired())
    }

    @Test fun `save replaces previous state and delete removes it`() {
        store.save(key, ConversationSession(key, "name"))
        store.save(key, ConversationSession(key, "city", initialValues = mapOf("name" to "Аня")))
        assertEquals("city", store.load(key)!!.step)
        assertEquals("Аня", store.load(key)!!["name"])
        store.delete(key)
        assertNull(store.load(key))
    }

    @Test fun `rejects incorrect keys and nonpositive timeout`() {
        assertThrows(IllegalArgumentException::class.java) { store.save(key, ConversationSession(key.copy(botId = "other"), "name")) }
        assertThrows(IllegalArgumentException::class.java) { store.save(key, ConversationSession(key, "name", 0)) }
    }
}
