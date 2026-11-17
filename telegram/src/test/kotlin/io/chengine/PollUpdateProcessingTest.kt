package io.chengine

import io.chengine.connector.BotResponseContext
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandlePoll
import io.chengine.handler.TelegramHandlePollAnswer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.polls.Poll
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer

@Handler
class PollUpdateHandler {
    val answers = mutableListOf<PollAnswer>()
    val polls = mutableListOf<Poll>()
    @TelegramHandlePollAnswer fun answer(update: Update) { answers.add(update.pollAnswer) }
    @TelegramHandlePoll fun poll(update: Update) { polls.add(update.poll ?: update.message.poll) }
}
private class PollTestBot : ChengineTelegramLongPoolingBot() {
    var replies = 0
    override fun executeResponse(response: BotResponseContext) { replies++ }
}
class PollUpdateProcessingTest {
    @Test fun `routes poll state votes retractions and voter chats without a message or session`() {
        val handler = PollUpdateHandler(); val bot = PollTestBot()
        Chengine(ChengineConfig(listOf(handler), listOf(bot)))
        val poll = Poll().apply { id = "p1"; totalVoterCount = 2 }
        val vote = PollAnswer().apply { pollId = "p1"; user = User(1L, "test", false); optionIds = listOf(1) }
        val retraction = PollAnswer().apply { pollId = "p1"; voterChat = Chat(42L, "group"); optionIds = emptyList() }
        bot.process(Update().apply { this.poll = poll })
        bot.process(Update().apply { pollAnswer = vote })
        bot.process(Update().apply { pollAnswer = retraction })
        assertSame(poll, handler.polls.single()); assertEquals(listOf(vote, retraction), handler.answers)
        assertTrue(handler.answers.last().optionIds.isEmpty()); assertEquals(42L, handler.answers.last().voterChat.id)
        assertEquals(0, bot.replies)
    }
}
