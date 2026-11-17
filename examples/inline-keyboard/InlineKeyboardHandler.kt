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
        text = "Нажмите кнопку, чтобы проверить callback."
        inlineKeyboard {
            row {
                button {
                    text = "Проверить кнопку"
                    callbackData = "check_button"
                }
            }
        }
    }

    @TelegramHandleCallback
    open fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
        callbackQueryId = update.callbackQuery.id
        text = if (update.callbackQuery.data == "check_button") "Кнопка работает!" else "Неизвестная кнопка"
        showAlert = true
    }
}
