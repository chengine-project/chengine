package io.chengine

import example.conversation.ConversationHandler
import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleText
import io.chengine.session.ConversationSession
import io.chengine.session.Conversations
import io.chengine.session.Session
import io.chengine.session.SessionKey
import io.chengine.session.SessionStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

private class TestBot : ChengineTelegramLongPoolingBot() {
    val replies = java.util.concurrent.ConcurrentLinkedQueue<String>()
    override fun executeResponse(response: BotResponseContext) { replies.add((response.currentResponseObject() as SendMessage).text) }
}
private fun update(text: String, user: Long = 1, chatId: Long = 10): Update = Update().apply {
    message = Message().apply { this.text = text; chat = Chat(chatId, "private"); from = User(user, "Test", false) }
}
private fun bot(name: String) = TestBot().also { it.telegramBotsLongPollingBotBot = TelegramBotsLongPoolingBot("test", name, it) }

/** Simulates a storage backend returning independent deserialized copies. */
private class CopyingStore : SessionStore {
    private val entries = ConcurrentHashMap<SessionKey, ConversationSession>()
    val saves = AtomicInteger()
    private fun copy(session: Session): ConversationSession {
        session as ConversationSession
        val (timeout, unit) = session.expirationTime()
        return ConversationSession(session.sessionKey(), session.step, timeout, unit, session.values())
    }
    override fun load(key: SessionKey): Session? = entries[key]?.let { copy(it) }
    override fun save(key: SessionKey, session: Session) { entries[key] = copy(session); saves.incrementAndGet() }
    override fun delete(key: SessionKey) { entries.remove(key) }
}

@Handler
class BlockingHandler {
    val entered = CountDownLatch(1)
    val release = CountDownLatch(1)
    val secondEntered = CountDownLatch(1)
    val otherEntered = CountDownLatch(1)
    val active = AtomicInteger()
    val maxActiveSameKey = AtomicInteger()
    @TelegramHandleText fun receive(request: BotRequestContext) = sendTelegramMessage {
        when (request.textContent()) {
            "first" -> {
                val n = active.incrementAndGet(); maxActiveSameKey.updateAndGet { maxOf(it, n) }
                entered.countDown()
                check(release.await(5, TimeUnit.SECONDS))
                active.decrementAndGet()
            }
            "second" -> {
                val n = active.incrementAndGet(); maxActiveSameKey.updateAndGet { maxOf(it, n) }
                secondEntered.countDown()
                active.decrementAndGet()
            }
            "other" -> otherEntered.countDown()
        }
        text = "ok"
    }
}

class SessionSafetyTest {
    @Test fun `serializes same session but permits another session concurrently`() {
        val handler = BlockingHandler(); val bot = bot("same_bot")
        Chengine(ChengineConfig(listOf(handler), listOf(bot)))
        val pool = Executors.newFixedThreadPool(3)
        try {
            val first = pool.submit { bot.process(update("first")) }
            assertTrue(handler.entered.await(3, TimeUnit.SECONDS))
            val second = pool.submit { bot.process(update("second")) }
            val other = pool.submit { bot.process(update("other", user = 2)) }
            assertTrue(handler.otherEntered.await(3, TimeUnit.SECONDS))
            assertFalse(handler.secondEntered.await(200, TimeUnit.MILLISECONDS))
            handler.release.countDown()
            first.get(3, TimeUnit.SECONDS); second.get(3, TimeUnit.SECONDS); other.get(3, TimeUnit.SECONDS)
            assertEquals(1, handler.maxActiveSameKey.get())
        } finally { handler.release.countDown(); pool.shutdownNow() }
    }
    @Test fun `two bot accounts keep separate sessions and recognize their own mentions`() {
        val one = bot("one_bot"); val two = bot("two_bot")
        Chengine(ChengineConfig(listOf(ConversationHandler()), listOf(one, two)))
        one.process(update("/start@one_bot")); two.process(update("/start@two_bot"))
        one.process(update("Alice")); two.process(update("Bob"))
        one.process(update("Moscow")); two.process(update("Kazan"))
        assertTrue(one.replies.last().contains("Alice")); assertTrue(two.replies.last().contains("Bob"))
        val previous = one.replies.size
        one.process(update("/start@two_bot"))
        assertEquals(previous, one.replies.size)
    }
    @Test fun `custom store receives updated detached conversation state`() {
        val store = CopyingStore(); val bot = bot("store_bot")
        Chengine(ChengineConfig(listOf(ConversationHandler()), listOf(bot), store))
        bot.process(update("/start")); bot.process(update("Alice")); bot.process(update("Moscow"))
        assertTrue(bot.replies.last().contains("Alice"))
        assertTrue(store.saves.get() >= 3)
        bot.process(update("next"))
        assertTrue(bot.replies.last().contains("/start"))
    }
}
