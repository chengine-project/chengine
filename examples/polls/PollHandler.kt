package example.polls

import io.chengine.command.HandleCommand
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
        question = "Что вам нравится больше?"
        options = listOf("Кошки", "Собаки")
        isAnonymous = false
    }

    @HandleCommand("/quiz") fun quiz() = sendTelegramPoll {
        question = "Сколько будет 2 + 2?"
        options = listOf("3", "4", "5")
        type = TelegramPollType.QUIZ
        correctOptionIds = listOf(1)
        explanation = "2 + 2 = 4"
        isAnonymous = false
    }

    @TelegramHandlePollAnswer fun onAnswer(update: Update) {
        val answer = update.pollAnswer
        // Persist or process answer.pollId, answer.optionIds and the optional voter identity here.
        println("Received answer for poll ${answer.pollId}: ${answer.optionIds}")
    }

    @TelegramHandlePoll fun onPollUpdate(update: Update) {
        val poll = update.poll ?: update.message?.poll ?: return
        println("Poll ${poll.id}: ${poll.totalVoterCount} votes")
    }
}
