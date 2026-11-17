package io.chengine

import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.dsl.sendTelegramPoll
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandlePollAnswer
import io.chengine.poll.InMemoryTelegramPollOriginStore
import io.chengine.poll.TelegramPollOrigin
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.polls.Poll
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.lang.reflect.Proxy

@Handler
class PollOriginHandler {
    var seen: TelegramPollOrigin? = null
    @HandleCommand("/poll") fun poll() = sendTelegramPoll { question = "Question?"; options = listOf("Yes", "No"); isAnonymous = false }
    @HandleCommand("/mixed") fun mixed() = listOf(sendTelegramMessage { text = "before" }, poll())
    @TelegramHandlePollAnswer fun answer(update: Update, request: BotRequestContext) = sendTelegramMessage {
        seen = request.telegramPollOrigin()
        text = if (update.pollAnswer.optionIds.isEmpty()) "retracted" else "thanks"
    }
}
class PollOriginProcessingTest {
    private fun client(sent: MutableList<Any>, pollId: String = "poll-1", fail: Boolean = false): TelegramClient =
        Proxy.newProxyInstance(TelegramClient::class.java.classLoader, arrayOf(TelegramClient::class.java)) { _, method, args ->
            require(method.name == "execute" && args?.size == 1)
            val value = args!![0]; sent.add(value)
            if (fail) throw IllegalStateException("failed")
            when (value) {
                is SendPoll -> Message().apply { messageId = 44; chat = Chat(123, "private"); poll = Poll().apply { id = pollId } }
                is SendMessage -> Message().apply { messageId = 45; chat = Chat(123, "private") }
                else -> error("Unexpected method")
            }
        } as TelegramClient
    private fun bot(name: String, sent: MutableList<Any>, handler: PollOriginHandler, store: InMemoryTelegramPollOriginStore = InMemoryTelegramPollOriginStore(), fail: Boolean = false): ChengineTelegramLongPoolingBot {
        val bot = ChengineTelegramLongPoolingBot()
        bot.pollOriginStore = store
        bot.telegramBotsLongPollingBotBot = TelegramBotsLongPoolingBot("token", name, bot, client(sent, fail = fail))
        Chengine(ChengineConfig(listOf(handler), listOf(bot)))
        return bot
    }
    private fun command(text: String) = Update().apply { message = Message().apply { this.text = text; chat = Chat(123, "private"); from = User(1, "A", false) } }
    private fun answer(pollId: String, indexes: List<Int>) = Update().apply { pollAnswer = PollAnswer().apply { this.pollId = pollId; optionIds = indexes; user = User(2, "B", false) } }

    @Test fun `poll answer and retraction reply to original chat`() {
        val sent = mutableListOf<Any>(); val handler = PollOriginHandler(); val bot = bot("BotOne", sent, handler)
        bot.process(command("/poll")); bot.process(answer("poll-1", listOf(1)))
        assertEquals(TelegramPollOrigin("poll-1", 123, 44), handler.seen)
        assertEquals("123", (sent.last() as SendMessage).chatId)
        assertEquals("thanks", (sent.last() as SendMessage).text)
        bot.process(answer("poll-1", emptyList()))
        assertEquals("retracted", (sent.last() as SendMessage).text)
    }
    @Test fun `captures polls in response lists and isolates bots sharing a store`() {
        val store = InMemoryTelegramPollOriginStore(); val sent = mutableListOf<Any>()
        val first = bot("bot-one", sent, PollOriginHandler(), store)
        val second = bot("bot-two", mutableListOf(), PollOriginHandler(), store)
        first.process(command("/mixed"))
        assertNotNull(store.get("bot-one", "poll-1"))
        assertNull(store.get("bot-two", "poll-1"))
        assertThrows(IllegalStateException::class.java) { second.process(answer("poll-1", listOf(0))) }
        store.remove("bot-one", "poll-1")
        assertNull(store.get("bot-one", "poll-1"))
    }
    @Test fun `failed poll send does not create a mapping and unknown poll fails clearly`() {
        val store = InMemoryTelegramPollOriginStore(); val bot = bot("bot", mutableListOf(), PollOriginHandler(), store, fail = true)
        assertThrows(IllegalStateException::class.java) { bot.process(command("/poll")) }
        assertNull(store.get("bot", "poll-1"))
        val unknown = assertThrows(IllegalStateException::class.java) { bot.process(answer("unknown", listOf(0))) }
        assertTrue(unknown.message!!.contains("chat ID"))
    }
    @Test fun `different poll ids route answers to their own chats`() {
        val sent = mutableListOf<Any>()
        val client = Proxy.newProxyInstance(TelegramClient::class.java.classLoader, arrayOf(TelegramClient::class.java)) { _, _, args ->
            val value = args!![0]; sent.add(value)
            when (value) {
                is SendPoll -> Message().apply {
                    messageId = 55; chat = Chat(value.chatId.toLong(), "private")
                    poll = Poll().apply { id = "poll-${value.chatId}" }
                }
                is SendMessage -> Message().apply { messageId = 56 }
                else -> error("Unexpected method")
            }
        } as TelegramClient
        val bot = ChengineTelegramLongPoolingBot()
        bot.telegramBotsLongPollingBotBot = TelegramBotsLongPoolingBot("token", "bot", bot, client)
        Chengine(ChengineConfig(listOf(PollOriginHandler()), listOf(bot)))
        bot.process(command("/poll"))
        bot.process(command("/poll").apply { message.chat = Chat(456, "private") })
        bot.process(answer("poll-456", listOf(0)))
        assertEquals("456", (sent.last() as SendMessage).chatId)
        bot.process(answer("poll-123", listOf(1)))
        assertEquals("123", (sent.last() as SendMessage).chatId)
    }
}
