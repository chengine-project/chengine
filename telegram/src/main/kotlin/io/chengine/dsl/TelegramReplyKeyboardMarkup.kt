package io.chengine.dsl

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

interface TelegramReplyKeyboardMarkupSupporter {
    var replyKeyboard: TelegramReplyKeyboard?
    fun replyKeyboard(block: TelegramReplyKeyboardBuilder.() -> Unit) {
        replyKeyboard = TelegramReplyKeyboardBuilder().apply(block)._build()
    }
}

@DslMarker annotation class TelegramReplyKeyboardMarkupDsl
@SendTelegramMessageDsl @TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboard : ArrayList<TelegramReplyKeyboardRow>() {
    internal var remove: Boolean = false
}
@SendTelegramMessageDsl @TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboardRow : ArrayList<TelegramReplyKeyboardButton>()
data class TelegramReplyKeyboardButton(var text: String, var requestContact: Boolean?, var requestLocation: Boolean?)

@SendTelegramMessageDsl @TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboardBuilder {
    private val rows = ArrayList<TelegramReplyKeyboardRow>()
    private var remove = false
    fun TelegramReplyKeyboardBuilder.row(block: TelegramReplyKeyboardRowBuilder.() -> Unit) {
        rows.add(TelegramReplyKeyboardRowBuilder().apply(block)._build())
    }
    fun removeKeyboard() { remove = true }
    fun _build(): TelegramReplyKeyboard {
        require(!remove || rows.isEmpty()) { "Cannot remove a keyboard and provide rows" }
        require(remove || (rows.isNotEmpty() && rows.all { it.isNotEmpty() })) { "Keyboard must contain nonempty rows" }
        return TelegramReplyKeyboard().apply { this.remove = this@TelegramReplyKeyboardBuilder.remove; addAll(this@TelegramReplyKeyboardBuilder.rows) }
    }
}
@SendTelegramMessageDsl @TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboardRowBuilder {
    private val buttons = ArrayList<TelegramReplyKeyboardButton>()
    fun TelegramReplyKeyboardRowBuilder.button(block: TelegramReplyKeyboardButtonBuilder.() -> Unit) {
        buttons.add(TelegramReplyKeyboardButtonBuilder().apply(block)._build())
    }
    fun _build() = TelegramReplyKeyboardRow().apply { addAll(this@TelegramReplyKeyboardRowBuilder.buttons) }
}
@SendTelegramMessageDsl @TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboardButtonBuilder {
    var text: String? = null
    var requestContact: Boolean? = null
    var requestLocation: Boolean? = null
    fun _build(): TelegramReplyKeyboardButton {
        val label = requireNotNull(text) { "Reply button text is required" }
        require(label.isNotBlank()) { "Reply button text must not be blank" }
        require(requestContact != true || requestLocation != true) { "A button cannot request both contact and location" }
        return TelegramReplyKeyboardButton(label, requestContact, requestLocation)
    }
}

fun TelegramReplyKeyboard?.toReplyMarkup(): ReplyKeyboard? = this?.let { keyboard ->
    if (keyboard.remove) ReplyKeyboardRemove.builder().removeKeyboard(true).build()
    else ReplyKeyboardMarkup.builder().keyboard(keyboard.map { row ->
        KeyboardRow().apply { row.forEach { button -> add(KeyboardButton.builder().text(button.text).build().apply {
            requestContact = button.requestContact
            requestLocation = button.requestLocation
        }) } }
    }).build()
}
