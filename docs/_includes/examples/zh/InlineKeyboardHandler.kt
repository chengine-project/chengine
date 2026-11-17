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
        text = "请测试按钮和文档链接。"
        inlineKeyboard {
            row {
                button {
                    text = "测试按钮"
                    callbackData = "check_button"
                }
                button {
                    text = "文档"
                    url = "https://chengine-project.github.io/chengine/"
                }
            }
        }
    }

    @TelegramHandleCallback
    open fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
        callbackQueryId = update.callbackQuery.id
        text = if (update.callbackQuery.data == "check_button") "按钮正常工作！" else "未知按钮"
        showAlert = true
    }
}
