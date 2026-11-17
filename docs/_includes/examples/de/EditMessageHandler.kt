package example.editmessage

import io.chengine.command.HandleCommand
import io.chengine.dsl.editTelegramMessageReplyMarkup
import io.chengine.dsl.editTelegramMessageText
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleCallback
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update

@Handler
@Component
class EditMessageHandler {
    @HandleCommand("/edit") fun start() = sendTelegramMessage {
        text = "Ursprüngliche Nachricht"
        inlineKeyboard { row { button { text = "Text ändern"; callbackData = "edit_text" } } }
    }

    @TelegramHandleCallback fun callback(update: Update): List<Any> {
        val query = update.callbackQuery
        val id = query.message.messageId
        val acknowledgement = AnswerCallbackQuery(query.id)
        return when (query.data) {
            "edit_text" -> listOf(acknowledgement, editTelegramMessageText {
                messageId = id
                text = "Text geändert"
                inlineKeyboard { row { button { text = "Tastatur ändern"; callbackData = "edit_keyboard" } } }
            })
            "edit_keyboard" -> listOf(acknowledgement, editTelegramMessageReplyMarkup {
                messageId = id
                inlineKeyboard { row { button { text = "Dokumentation öffnen"; url = "https://chengine-project.github.io/chengine/" } } }
            })
            else -> listOf(acknowledgement)
        }
    }
}
