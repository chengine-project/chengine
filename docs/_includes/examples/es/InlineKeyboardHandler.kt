package example

import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleCallback
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update

@Handler
@Component
open class InlineKeyboardHandler {
    @HandleCommand("/start")
    open fun start() = sendTelegramMessage {
        text = "Prueba el botón y el enlace a la documentación."
        inlineKeyboard {
            row {
                button {
                    text = "Probar botón"
                    callbackData = "check_button"
                }
                button {
                    text = "Documentación"
                    url = "https://chengine-project.github.io/chengine/"
                }
            }
        }
    }

    @TelegramHandleCallback
    open fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
        callbackQueryId = update.callbackQuery.id
        text = if (update.callbackQuery.data == "check_button") "¡El botón funciona!" else "Botón desconocido"
        showAlert = true
    }
}
