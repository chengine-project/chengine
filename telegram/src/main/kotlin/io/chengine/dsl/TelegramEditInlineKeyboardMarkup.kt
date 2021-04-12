package io.chengine.dsl

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

fun editInlineKeyboardMarkup(block: EditInlineKeyboardMarkupBuilder.() -> Unit): InlineKeyboardMarkup =
    EditInlineKeyboardMarkupBuilder().apply(block)._build()

class EditInlineKeyboardMarkupBuilder {

    private var deleteKeyboard = false

    fun deleteKeyboard() { deleteKeyboard = true }
    fun deleteRow(index: Int) {}

    fun _build(): InlineKeyboardMarkup {
        TODO()
    }

    fun editButton(atRow: Int, withIndex: Int, block: ChangeButtonBuilder.() -> Unit): Unit = ChangeButtonBuilder().apply(block)._build()
}

class ChangeButtonBuilder {

    var text: String? = null
    var url: String? = null
    var callbackData: String? = null

    fun _build() {
        TODO()
    }
}