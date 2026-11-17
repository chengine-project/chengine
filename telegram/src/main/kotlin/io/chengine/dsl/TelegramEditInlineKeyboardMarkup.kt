package io.chengine.dsl

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow

/** Modify a copy of an existing keyboard before passing it to a Telegram edit request. */
fun editInlineKeyboardMarkup(original: InlineKeyboardMarkup, block: EditInlineKeyboardMarkupBuilder.() -> Unit): InlineKeyboardMarkup =
    EditInlineKeyboardMarkupBuilder(original).apply(block)._build()

/** With no original keyboard only deleteKeyboard() is meaningful. */
fun editInlineKeyboardMarkup(block: EditInlineKeyboardMarkupBuilder.() -> Unit): InlineKeyboardMarkup =
    EditInlineKeyboardMarkupBuilder().apply(block)._build()

class EditInlineKeyboardMarkupBuilder(original: InlineKeyboardMarkup? = null) {
    private val rows = original?.keyboard?.map { it.toMutableList() }?.toMutableList() ?: mutableListOf()
    fun deleteKeyboard() { rows.clear() }
    fun deleteRow(index: Int) { require(index in rows.indices) { "Row index out of range" }; rows.removeAt(index) }
    fun editButton(atRow: Int, withIndex: Int, block: ChangeButtonBuilder.() -> Unit) {
        require(atRow in rows.indices && withIndex in rows[atRow].indices) { "Button index out of range" }
        rows[atRow][withIndex] = ChangeButtonBuilder().apply(block)._build(rows[atRow][withIndex])
    }
    fun _build(): InlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(rows.map { row ->
        InlineKeyboardRow().apply { addAll(row) }
    }).build()
}

class ChangeButtonBuilder {
    var text: String? = null
    var url: String? = null
    var callbackData: String? = null
    fun _build(original: InlineKeyboardButton): InlineKeyboardButton {
        require(text == null || text!!.isNotBlank()) { "Button text must not be blank" }
        require(url == null || url!!.isNotBlank()) { "Button URL must not be blank" }
        require(url == null || callbackData == null) { "Choose either URL or callback data" }
        require(callbackData == null || callbackData!!.toByteArray(Charsets.UTF_8).size in 1..64) { "Callback data must be 1 to 64 UTF-8 bytes" }
        return InlineKeyboardButton.builder().text(text ?: original.text).build().apply {
            iconCustomEmojiId = original.iconCustomEmojiId
            style = original.style
            if (this@ChangeButtonBuilder.url != null) this.url = this@ChangeButtonBuilder.url
            else if (this@ChangeButtonBuilder.callbackData != null) this.callbackData = this@ChangeButtonBuilder.callbackData
            else {
                url = original.url; callbackData = original.callbackData; callbackGame = original.callbackGame
                switchInlineQuery = original.switchInlineQuery; switchInlineQueryCurrentChat = original.switchInlineQueryCurrentChat
                pay = original.pay; loginUrl = original.loginUrl; webApp = original.webApp
                switchInlineQueryChosenChat = original.switchInlineQueryChosenChat; copyText = original.copyText; disabled = original.disabled
            }
        }
    }
}
