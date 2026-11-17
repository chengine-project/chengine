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
    Product("☕ 咖啡", "中度烘焙咖啡豆，250 克。"),
    Product("🍵 茶", "绿茶茶叶，100 克。"),
    Product("🍫 巧克力", "可可含量 70% 的黑巧克力。"),
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
            return listOf(AnswerCallbackQuery(query.id).apply { text = "无法打开此项目"; showAlert = true })
        }
        val productIndex = requireNotNull(index)
        return listOf(AnswerCallbackQuery(query.id), editTelegramMessageText {
            this.messageId = messageId
            text = if (type == "item") itemText(productIndex) else pageText(productIndex)
            inlineKeyboard { if (type == "item") itemButtons(productIndex) else pageButtons(productIndex) }
        })
    }
}

private fun pageText(index: Int) = "商品目录 · ${index + 1}/${products.size}\n${products[index].name}\n请选择商品或翻阅目录。"
private fun itemText(index: Int) = "${products[index].name}\n${products[index].description}"

private fun TelegramInlineKeyboardBuilder.pageButtons(index: Int) {
    row {
        if (index > 0) button { text = "⬅️ 返回"; callbackData = "catalog:page:${index - 1}" }
        if (index < products.lastIndex) button { text = "前进 ➡️"; callbackData = "catalog:page:${index + 1}" }
    }
    row { button { text = "查看详情"; callbackData = "catalog:item:$index" } }
    row { button { text = "文档"; url = "https://chengine-project.github.io/chengine/" } }
}

private fun TelegramInlineKeyboardBuilder.itemButtons(index: Int) {
    row { button { text = "⬅️ 返回目录"; callbackData = "catalog:page:$index" } }
    row { button { text = "文档"; url = "https://chengine-project.github.io/chengine/" } }
}
