package example.conversation

import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleText
import io.chengine.session.Conversations
import org.springframework.stereotype.Component

@Handler
@Component
open class ConversationHandler {
    @HandleCommand("/start")
    open fun start(request: BotRequestContext) = sendTelegramMessage {
        if (request.sessionKey() == null) {
            text = "Не удалось определить пользователя и чат."
        } else {
            Conversations.start(request, "name")
            text = "Как вас зовут? Для отмены отправьте /cancel."
        }
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
        val answer = request.textContent()?.trim().orEmpty()
        text = when {
            session == null -> "Начните диалог командой /start."
            answer.isEmpty() -> "Пожалуйста, отправьте непустой ответ."
            session.step == "name" -> {
                session["name"] = answer
                session.step = "city"
                "В каком вы городе?"
            }
            session.step == "city" -> {
                val name = session["name"] ?: ""
                Conversations.end()
                "Приятно познакомиться, $name! Ваш город: $answer."
            }
            else -> {
                Conversations.end()
                "Не удалось продолжить диалог. Отправьте /start."
            }
        }
    }
}
