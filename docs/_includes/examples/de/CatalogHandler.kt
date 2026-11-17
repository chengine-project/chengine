package example.catalog

import io.chengine.command.HandleCommand
import io.chengine.dsl.TelegramInlineKeyboardBuilder
import io.chengine.dsl.editTelegramMessageText
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleCallback
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.objects.Update

private data class Product(val name: String, val description: String)
private val products = listOf(
    Product("☕ Kaffee", "Bohnen mittlerer Röstung, 250 g."),
    Product("🍵 Tee", "Grüner loser Tee, 100 g."),
    Product("🍫 Schokolade", "Dunkle Schokolade mit 70 % Kakao."),
)

@Handler
@Component
class CatalogHandler {
    @HandleCommand("/catalog") fun start() = sendTelegramMessage {
        text = pageText(0)
        inlineKeyboard { pageButtons(0) }
    }

    @TelegramHandleCallback fun callback(update: Update): List<Any> {
        val query = update.callbackQuery
        val parts = query.data?.split(":").orEmpty()
        val index = parts.getOrNull(2)?.toIntOrNull()
        val type = parts.getOrNull(1)
        val messageId = query.message?.messageId
        if (parts.size != 3 || parts[0] != "catalog" || type !in setOf("page", "item") || index !in products.indices || messageId == null) {
            return listOf(AnswerCallbackQuery(query.id).apply { text = "Dieser Eintrag konnte nicht geöffnet werden"; showAlert = true })
        }
        val productIndex = requireNotNull(index)
        return listOf(AnswerCallbackQuery(query.id), editTelegramMessageText {
            this.messageId = messageId
            text = if (type == "item") itemText(productIndex) else pageText(productIndex)
            inlineKeyboard { if (type == "item") itemButtons(productIndex) else pageButtons(productIndex) }
        })
    }
}

private fun pageText(index: Int) = "Katalog · ${index + 1}/${products.size}\n${products[index].name}\nWähle ein Produkt oder blättere im Katalog."
private fun itemText(index: Int) = "${products[index].name}\n${products[index].description}"

private fun TelegramInlineKeyboardBuilder.pageButtons(index: Int) {
    row {
        if (index > 0) button { text = "⬅️ Zurück"; callbackData = "catalog:page:${index - 1}" }
        if (index < products.lastIndex) button { text = "Weiter ➡️"; callbackData = "catalog:page:${index + 1}" }
    }
    row { button { text = "Details"; callbackData = "catalog:item:$index" } }
    row { button { text = "Dokumentation"; url = "https://chengine-project.github.io/chengine/" } }
}

private fun TelegramInlineKeyboardBuilder.itemButtons(index: Int) {
    row { button { text = "⬅️ Zum Katalog"; callbackData = "catalog:page:$index" } }
    row { button { text = "Dokumentation"; url = "https://chengine-project.github.io/chengine/" } }
}
