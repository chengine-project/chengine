package example.polls

import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.telegramPollOrigin
import io.chengine.dsl.sendTelegramMessage
import io.chengine.dsl.TelegramPollType
import io.chengine.dsl.sendTelegramPoll
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandlePoll
import io.chengine.handler.TelegramHandlePollAnswer
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

@Handler
@Component
class PollHandler {
    @HandleCommand("/poll") fun poll() = sendTelegramPoll {
        question = "Which do you prefer?"
        options = listOf("Cats", "Dogs")
        isAnonymous = false
    }

    @HandleCommand("/quiz") fun quiz() = sendTelegramPoll {
        question = "What is 2 + 2?"
        options = listOf("3", "4", "5")
        type = TelegramPollType.QUIZ
        correctOptionIds = listOf(1)
        explanation = "2 + 2 = 4"
        isAnonymous = false
    }

    @TelegramHandlePollAnswer fun onAnswer(update: Update, request: BotRequestContext) =
        if (request.telegramPollOrigin() != null) sendTelegramMessage {
            text = if (update.pollAnswer.optionIds.isEmpty()) "Vote retracted." else "Thanks for your answer!"
        } else Unit // A mapping may be unavailable after restart or for a poll sent elsewhere.

    @TelegramHandlePoll fun onPollUpdate(update: Update) {
        val poll = update.poll ?: update.message?.poll ?: return
        println("Poll ${poll.id}: ${poll.totalVoterCount} votes")
    }
}
