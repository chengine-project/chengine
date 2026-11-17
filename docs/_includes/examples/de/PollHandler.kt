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
        question = "Was gefällt dir besser?"
        options = listOf("Katzen", "Hunde")
        isAnonymous = false
    }

    @HandleCommand("/quiz") fun quiz() = sendTelegramPoll {
        question = "Wie viel ist 2 + 2?"
        options = listOf("3", "4", "5")
        type = TelegramPollType.QUIZ
        correctOptionIds = listOf(1)
        explanation = "2 + 2 = 4"
        isAnonymous = false
    }

    @TelegramHandlePollAnswer fun onAnswer(update: Update, request: BotRequestContext) =
        if (request.telegramPollOrigin() != null) sendTelegramMessage {
            text = if (update.pollAnswer.optionIds.isEmpty()) "Stimme zurückgezogen." else "Danke für deine Antwort!"
        } else Unit // Nach einem Neustart oder bei einer anderswo gesendeten Umfrage kann die Zuordnung fehlen.

    @TelegramHandlePoll fun onPollUpdate(update: Update) {
        val poll = update.poll ?: update.message?.poll ?: return
        println("Umfrage ${poll.id}: ${poll.totalVoterCount} Stimmen")
    }
}
