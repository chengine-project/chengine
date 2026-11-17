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
        "start" to { _, _ -> GraphTransition.Await("name", "Как вас зовут? Для отмены отправьте /cancel.") },
        "name" to { session, input ->
            val name = input.orEmpty().trim()
            if (name.isEmpty()) GraphTransition.Await("name", "Пожалуйста, отправьте непустой ответ.")
            else {
                session["name"] = name
                GraphTransition.Await("city", "В каком вы городе? Можно написать «пропустить».")
            }
        },
        "city" to { session, input ->
            val city = input.orEmpty().trim()
            when {
                city.isEmpty() -> GraphTransition.Await("city", "Пожалуйста, отправьте непустой ответ.")
                city.equals("пропустить", ignoreCase = true) -> GraphTransition.Next("withoutCity")
                else -> { session["city"] = city; GraphTransition.Next("withCity") }
            }
        },
        "withCity" to { session, _ -> GraphTransition.Finish("Приятно познакомиться, ${session["name"]}! Ваш город: ${session["city"]}.") },
        "withoutCity" to { session, _ -> GraphTransition.Finish("Приятно познакомиться, ${session["name"]}!") }
    ))

    @HandleCommand("/start")
    open fun start(request: BotRequestContext) = sendTelegramMessage {
        text = if (request.sessionKey() == null) "Не удалось определить пользователя и чат."
        else graph.start(Conversations.start(request, "start")).message
    }

    @HandleCommand("/cancel")
    open fun cancel() = sendTelegramMessage {
        val active = Conversations.current() != null
        Conversations.end()
        text = if (active) "Диалог отменён." else "Активного диалога нет."
    }

    @TelegramHandleText
    open fun answer(request: BotRequestContext) = sendTelegramMessage {
        val session = Conversations.current()
        text = if (session == null) "Начните диалог командой /start."
        else {
            val result = graph.resume(session, request.textContent().orEmpty())
            if (result.completed) Conversations.end()
            result.message
        }
    }
}
