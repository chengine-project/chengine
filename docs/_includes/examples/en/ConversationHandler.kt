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
        "start" to { _, _ -> GraphTransition.Await("name", "What is your name? Send /cancel to cancel.") },
        "name" to { session, input ->
            val name = input.orEmpty().trim()
            if (name.isEmpty()) GraphTransition.Await("name", "Please send a non-empty answer.")
            else {
                session["name"] = name
                GraphTransition.Await("city", "Which city are you in? Send skip to leave it blank.")
            }
        },
        "city" to { session, input ->
            val city = input.orEmpty().trim()
            when {
                city.isEmpty() -> GraphTransition.Await("city", "Please send a non-empty answer.")
                city.equals("skip", ignoreCase = true) -> GraphTransition.Next("withoutCity")
                else -> { session["city"] = city; GraphTransition.Next("withCity") }
            }
        },
        "withCity" to { session, _ -> GraphTransition.Finish("Nice to meet you, ${session["name"]}! Your city: ${session["city"]}.") },
        "withoutCity" to { session, _ -> GraphTransition.Finish("Nice to meet you, ${session["name"]}!") }
    ))

    @HandleCommand("/start")
    open fun start(request: BotRequestContext) = sendTelegramMessage {
        text = if (request.sessionKey() == null) "Could not determine the user and chat."
        else graph.start(Conversations.start(request, "start")).message
    }

    @HandleCommand("/cancel")
    open fun cancel() = sendTelegramMessage {
        val active = Conversations.current() != null
        Conversations.end()
        text = if (active) "Conversation canceled." else "No active conversation."
    }

    @TelegramHandleText
    open fun answer(request: BotRequestContext) = sendTelegramMessage {
        val session = Conversations.current()
        text = if (session == null) "Start a conversation with /start."
        else {
            val result = graph.resume(session, request.textContent().orEmpty())
            if (result.completed) Conversations.end()
            result.message
        }
    }
}
