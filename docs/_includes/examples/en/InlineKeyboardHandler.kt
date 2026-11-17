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
        text = "Try the button and the documentation link."
        inlineKeyboard {
            row {
                button {
                    text = "Test button"
                    callbackData = "check_button"
                }
                button {
                    text = "Documentation"
                    url = "https://chengine-project.github.io/chengine/"
                }
            }
        }
    }

    @TelegramHandleCallback
    open fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
        callbackQueryId = update.callbackQuery.id
        text = if (update.callbackQuery.data == "check_button") "The button works!" else "Unknown button"
        showAlert = true
    }
}
