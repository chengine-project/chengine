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
}

fun changeButton(block: ChangeButtonBuilder.() -> Unit): Unit = ChangeButtonBuilder().apply(block)._build()

class ChangeButtonBuilder {

    var row: Int? = null
    var index: Int? = null
    var text: String? = null
    var url: String? = null

    fun _build() {
        TODO()
    }
}