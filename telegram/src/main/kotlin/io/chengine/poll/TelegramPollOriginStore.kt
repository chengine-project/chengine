package io.chengine.poll

import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit

/** Location of a poll sent by a specific bot. */
data class TelegramPollOrigin(val pollId: String, val chatId: Long, val messageId: Int)

/** Implement with durable storage if poll responses must survive a bot restart. */
interface TelegramPollOriginStore {
    fun put(botId: String, origin: TelegramPollOrigin)
    fun get(botId: String, pollId: String): TelegramPollOrigin?
    fun remove(botId: String, pollId: String)
}

/** Bounded process-local store; entries expire 30 days after sending. */
class InMemoryTelegramPollOriginStore : TelegramPollOriginStore {
    private val cache = Caffeine.newBuilder().maximumSize(100_000).expireAfterWrite(30, TimeUnit.DAYS)
        .build<Pair<String, String>, TelegramPollOrigin>()
    override fun put(botId: String, origin: TelegramPollOrigin) { cache.put(botId to origin.pollId, origin) }
    override fun get(botId: String, pollId: String): TelegramPollOrigin? = cache.getIfPresent(botId to pollId)
    override fun remove(botId: String, pollId: String) { cache.invalidate(botId to pollId) }
}
