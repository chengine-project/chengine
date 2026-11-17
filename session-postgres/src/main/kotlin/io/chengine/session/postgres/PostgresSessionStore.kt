package io.chengine.session.postgres

import io.chengine.session.ConversationSession
import io.chengine.session.Session
import io.chengine.session.SessionKey
import io.chengine.session.SessionStore
import java.io.StringReader
import java.io.StringWriter
import java.sql.PreparedStatement
import java.util.Properties
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

/** Stores ConversationSession snapshots in PostgreSQL. Create the table with [initializeSchema] once. */
class PostgresSessionStore(private val dataSource: DataSource) : SessionStore {
    fun initializeSchema() {
        dataSource.connection.use { connection ->
            connection.createStatement().use { statement -> statement.executeUpdate(SCHEMA) }
        }
    }

    override fun load(key: SessionKey): ConversationSession? = dataSource.connection.use { connection ->
        connection.prepareStatement(LOAD).use { statement ->
            statement.bindKey(key)
            statement.executeQuery().use { result ->
                if (!result.next()) return@use null
                val properties = Properties().apply { load(StringReader(result.getString("session_values"))) }
                ConversationSession(key, result.getString("step"), result.getInt("timeout_amount"),
                    TimeUnit.valueOf(result.getString("timeout_unit")),
                    properties.stringPropertyNames().associateWith { properties.getProperty(it) })
            }
        }
    }

    override fun save(key: SessionKey, session: Session) {
        require(session is ConversationSession) { "PostgresSessionStore only supports ConversationSession" }
        require(session.sessionKey() == key) { "Session key does not match" }
        val (amount, unit) = session.expirationTime()
        require(amount > 0) { "Session expiration must be positive" }
        val timeoutMillis = when (unit) {
            TimeUnit.NANOSECONDS -> (amount.toLong() + 999_999) / 1_000_000
            TimeUnit.MICROSECONDS -> (amount.toLong() + 999) / 1_000
            else -> unit.toMillis(amount.toLong())
        }
        val writer = StringWriter()
        Properties().apply { session.values().forEach { (name, value) -> setProperty(name, value) } }.store(writer, null)
        dataSource.connection.use { connection ->
            connection.prepareStatement(SAVE).use { statement ->
                statement.bindKey(key)
                statement.setString(7, session.step)
                statement.setString(8, writer.toString())
                statement.setInt(9, amount)
                statement.setString(10, unit.name)
                statement.setLong(11, timeoutMillis)
                statement.setLong(12, timeoutMillis)
                statement.executeUpdate()
            }
        }
    }

    override fun delete(key: SessionKey) {
        dataSource.connection.use { connection ->
            connection.prepareStatement(DELETE).use { statement -> statement.bindKey(key); statement.executeUpdate() }
        }
    }

    /** Removes expired records; expired sessions are never returned even before cleanup. */
    fun deleteExpired(): Int = dataSource.connection.use { connection ->
        connection.createStatement().use { it.executeUpdate("DELETE FROM chengine_sessions WHERE expires_at <= clock_timestamp()") }
    }

    private fun PreparedStatement.bindKey(key: SessionKey) {
        setString(1, key.botApiIdentifier)
        setString(2, key.botId)
        setBoolean(3, key.userId != null)
        setString(4, key.userId ?: "")
        setBoolean(5, key.chatId != null)
        setString(6, key.chatId ?: "")
    }

    companion object {
        private const val KEY = "bot_api = ? AND bot_id = ? AND user_present = ? AND user_id = ? AND chat_present = ? AND chat_id = ?"
        private const val SCHEMA = """CREATE TABLE IF NOT EXISTS chengine_sessions (
            bot_api TEXT NOT NULL, bot_id TEXT NOT NULL, user_present BOOLEAN NOT NULL, user_id TEXT NOT NULL,
            chat_present BOOLEAN NOT NULL, chat_id TEXT NOT NULL, step TEXT NOT NULL, session_values TEXT NOT NULL,
            timeout_amount INTEGER NOT NULL, timeout_unit TEXT NOT NULL, timeout_millis BIGINT NOT NULL,
            expires_at TIMESTAMPTZ NOT NULL,
            PRIMARY KEY (bot_api, bot_id, user_present, user_id, chat_present, chat_id))"""
        private const val LOAD = """UPDATE chengine_sessions SET expires_at = clock_timestamp() + timeout_millis * interval '1 millisecond'
            WHERE $KEY AND expires_at > clock_timestamp() RETURNING step, session_values, timeout_amount, timeout_unit"""
        private const val SAVE = """INSERT INTO chengine_sessions
            (bot_api, bot_id, user_present, user_id, chat_present, chat_id, step, session_values, timeout_amount, timeout_unit, timeout_millis, expires_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, clock_timestamp() + ? * interval '1 millisecond')
            ON CONFLICT (bot_api, bot_id, user_present, user_id, chat_present, chat_id) DO UPDATE SET
            step = EXCLUDED.step, session_values = EXCLUDED.session_values, timeout_amount = EXCLUDED.timeout_amount,
            timeout_unit = EXCLUDED.timeout_unit, timeout_millis = EXCLUDED.timeout_millis, expires_at = EXCLUDED.expires_at"""
        private const val DELETE = "DELETE FROM chengine_sessions WHERE $KEY"
    }
}
