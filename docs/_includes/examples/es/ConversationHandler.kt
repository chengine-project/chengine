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
        "start" to { _, _ -> GraphTransition.Await("name", "¿Cómo te llamas? Para cancelar, envía /cancel.") },
        "name" to { session, input ->
            val name = input.orEmpty().trim()
            if (name.isEmpty()) GraphTransition.Await("name", "Envía una respuesta que no esté vacía.")
            else {
                session["name"] = name
                GraphTransition.Await("city", "¿En qué ciudad estás? Puedes escribir «omitir».")
            }
        },
        "city" to { session, input ->
            val city = input.orEmpty().trim()
            when {
                city.isEmpty() -> GraphTransition.Await("city", "Envía una respuesta que no esté vacía.")
                city.equals("omitir", ignoreCase = true) -> GraphTransition.Next("withoutCity")
                else -> { session["city"] = city; GraphTransition.Next("withCity") }
            }
        },
        "withCity" to { session, _ -> GraphTransition.Finish("¡Mucho gusto, ${session["name"]}! Tu ciudad es: ${session["city"]}.") },
        "withoutCity" to { session, _ -> GraphTransition.Finish("¡Mucho gusto, ${session["name"]}!") }
    ))

    @HandleCommand("/start")
    open fun start(request: BotRequestContext) = sendTelegramMessage {
        text = if (request.sessionKey() == null) "No se pudieron identificar el usuario y el chat."
        else graph.start(Conversations.start(request, "start")).message
    }

    @HandleCommand("/cancel")
    open fun cancel() = sendTelegramMessage {
        val active = Conversations.current() != null
        Conversations.end()
        text = if (active) "Diálogo cancelado." else "No hay ningún diálogo activo."
    }

    @TelegramHandleText
    open fun answer(request: BotRequestContext) = sendTelegramMessage {
        val session = Conversations.current()
        text = if (session == null) "Inicia el diálogo con /start."
        else {
            val result = graph.resume(session, request.textContent().orEmpty())
            if (result.completed) Conversations.end()
            result.message
        }
    }
}
