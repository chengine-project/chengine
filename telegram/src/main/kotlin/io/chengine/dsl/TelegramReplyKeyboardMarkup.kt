package io.chengine.dsl

interface TelegramReplyKeyboardMarkupSupporter {
    var replyKeyboard: TelegramReplyKeyboard?
    fun replyKeyboard(block: TelegramReplyKeyboardBuilder.() -> Unit) {
        replyKeyboard = TelegramReplyKeyboardBuilder().apply(block)._build()
    }
}

@DslMarker
annotation class TelegramReplyKeyboardMarkupDsl

@SendTelegramMessageDsl
@TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboard: ArrayList<TelegramReplyKeyboardRow>()

@SendTelegramMessageDsl
@TelegramReplyKeyboardMarkupDsl
class TelegramReplyKeyboardRow: ArrayList<TelegramReplyKeyboardButton>()

data class TelegramReplyKeyboardButton(
    var text: String,
    var requestContact: Boolean?,
    var requestLocation: Boolean?,
)

// Builders ********************************************************************************************************

class TelegramReplyKeyboardBuilder {
    private val rows: ArrayList<TelegramReplyKeyboardRow> = ArrayList()
    fun _build() = TelegramReplyKeyboard().apply {
        rows.forEach { add(it) }
    }

    fun removeKeyboard() { TODO() }
}

class TelegramReplyKeyboardRowBuilder {
    private val buttons: ArrayList<TelegramReplyKeyboardButton> = ArrayList()
    fun _build() = TelegramReplyKeyboardRow().apply {
        buttons.forEach { add(it) }
    }

    fun TelegramReplyKeyboardRowBuilder.button(block: TelegramReplyKeyboardButtonBuilder.() -> Unit) {
        buttons.add(TelegramReplyKeyboardButtonBuilder().apply(block)._build())
    }
}

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramReplyKeyboardButtonBuilder {

    var text: String? = null
    var requestContact: Boolean? = null
    var requestLocation: Boolean? = null

    fun _build() = TelegramReplyKeyboardButton(
        text = text ?: throw RuntimeException("Reply button text can't be null"),
        requestContact = requestContact,
        requestLocation = requestLocation
    )
}

// ************************************************************************************************************