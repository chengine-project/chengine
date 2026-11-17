package example.conversation

import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.graph.ConversationGraph
import io.chengine.graph.GraphTransition
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleText
import io.chengine.session.Conversations
import org.springframework.stereotype.Component

@Handler
@Component
open class ConversationHandler {
    private val graph = ConversationGraph("start", mapOf(
        "start" to { _, _ -> GraphTransition.Await("name", "Wie heißt du? Sende /cancel zum Abbrechen.") },
        "name" to { session, input ->
            val name = input.orEmpty().trim()
            if (name.isEmpty()) GraphTransition.Await("name", "Bitte sende eine Antwort.")
            else {
                session["name"] = name
                GraphTransition.Await("city", "In welcher Stadt bist du? Sende überspringen, um sie auszulassen.")
            }
        },
        "city" to { session, input ->
            val city = input.orEmpty().trim()
            when {
                city.isEmpty() -> GraphTransition.Await("city", "Bitte sende eine Antwort.")
                city.equals("überspringen", ignoreCase = true) -> GraphTransition.Next("withoutCity")
                else -> { session["city"] = city; GraphTransition.Next("withCity") }
            }
        },
        "withCity" to { session, _ -> GraphTransition.Finish("Schön, dich kennenzulernen, ${session["name"]}! Deine Stadt: ${session["city"]}.") },
        "withoutCity" to { session, _ -> GraphTransition.Finish("Schön, dich kennenzulernen, ${session["name"]}!") }
    ))

    @HandleCommand("/start")
    open fun start(request: BotRequestContext) = sendTelegramMessage {
        text = if (request.sessionKey() == null) "Benutzer und Chat konnten nicht ermittelt werden."
        else graph.start(Conversations.start(request, "start")).message
    }

    @HandleCommand("/cancel")
    open fun cancel() = sendTelegramMessage {
        val active = Conversations.current() != null
        Conversations.end()
        text = if (active) "Dialog abgebrochen." else "Kein aktiver Dialog."
    }

    @TelegramHandleText
    open fun answer(request: BotRequestContext) = sendTelegramMessage {
        val session = Conversations.current()
        text = if (session == null) "Beginne einen Dialog mit /start."
        else {
            val result = graph.resume(session, request.textContent().orEmpty())
            if (result.completed) Conversations.end()
            result.message
        }
    }
}
