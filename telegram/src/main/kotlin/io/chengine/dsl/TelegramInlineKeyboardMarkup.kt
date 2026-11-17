package io.chengine.dsl

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow

// Telegram Inline Keyboard

@DslMarker
annotation class TelegramInlineKeyboardDsl

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramInlineKeyboard: ArrayList<TelegramInlineKeyboardRow>()

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramInlineKeyboardRow: ArrayList<TelegramInlineKeyboardButton>()

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
data class TelegramInlineKeyboardButton(
    val text: String,
    val callbackData: String?,
    val url: String?,
    val switchInlineQuery: String?,
    val switchInlineQueryCurrentChat: String?,
    val pay: Boolean?
) {
    init {
        require(text.isNotBlank()) { "Button text must not be blank" }
        require(pay != false) { "Button pay must be true when specified" }
        require(listOf(callbackData, url, switchInlineQuery, switchInlineQueryCurrentChat, pay).count { it != null } == 1) {
            "Button must have exactly one action"
        }
        require(callbackData == null || callbackData.toByteArray(Charsets.UTF_8).size in 1..64) {
            "Button callback data must be 1 to 64 UTF-8 bytes"
        }
        require(url == null || url.isNotBlank()) { "Button URL must not be blank" }
    }
}

// Builders ******************************************************************************************************

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramInlineKeyboardBuilder {
    private val rows: ArrayList<TelegramInlineKeyboardRow> = ArrayList()
    fun _build(): TelegramInlineKeyboard {
        val telegramInlineKeyboard = TelegramInlineKeyboard()
        rows.forEach { telegramInlineKeyboard.add(it) }
        return telegramInlineKeyboard
    }
    fun TelegramInlineKeyboardBuilder.row(block: TelegramInlineKeyboardRowBuilder.() -> Unit) {
        rows.add(TelegramInlineKeyboardRowBuilder().apply(block)._build())
    }
}

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramInlineKeyboardRowBuilder {
    private val buttons: ArrayList<TelegramInlineKeyboardButton> = ArrayList()
    fun _build(): TelegramInlineKeyboardRow {
        val telegramInlineKeyboardRow = TelegramInlineKeyboardRow()
        buttons.forEach { telegramInlineKeyboardRow.add(it) }
        return telegramInlineKeyboardRow
    }

    fun TelegramInlineKeyboardRowBuilder.button(block: TelegramInlineKeyboardButtonBuilder.() -> Unit) {
        buttons.add(TelegramInlineKeyboardButtonBuilder().apply(block)._build())
    }
}

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramInlineKeyboardButtonBuilder {

    var text: String? = null
    var callbackData: String? = null
    var url: String? = null
    var switchInlineQuery: String? = null
    var switchInlineQueryCurrentChat: String? = null
    var pay: Boolean? = null

    fun _build() = TelegramInlineKeyboardButton(
        text ?: throw RuntimeException("Button text can't be null"),
        callbackData,
        url,
        switchInlineQuery,
        switchInlineQueryCurrentChat,
        pay
    )
}

// ************************************************************************************************************

interface InlineKeyboardSupporter {
    var inlineKeyboard: TelegramInlineKeyboard?
    fun inlineKeyboard(block: TelegramInlineKeyboardBuilder.() -> Unit) {
        inlineKeyboard = TelegramInlineKeyboardBuilder().apply(block)._build()
    }
}

fun TelegramInlineKeyboard?.toReplyMarkup() = this?.let { keyboard ->
    val telegramInlineKeyboardMarkup = InlineKeyboardMarkup.builder().keyboard(emptyList()).build()
    val telegramInlineKeyboardRows = ArrayList<InlineKeyboardRow>()
    keyboard.forEach { row ->
        val telegramInlineKeyboardRow = InlineKeyboardRow()
        row.forEach { button ->
            val telegramInlineKeyboardButton = InlineKeyboardButton.builder().text(button.text).build()
            telegramInlineKeyboardButton.text = button.text
            button.callbackData?.let { telegramInlineKeyboardButton.callbackData = it }
            button.switchInlineQueryCurrentChat?.let { telegramInlineKeyboardButton.switchInlineQueryCurrentChat = it }
            button.switchInlineQuery?.let { telegramInlineKeyboardButton.switchInlineQuery = it }
            button.pay?.let { telegramInlineKeyboardButton.pay = it }
            button.url?.let { telegramInlineKeyboardButton.url = it }
            telegramInlineKeyboardRow.add(telegramInlineKeyboardButton)
        }
        telegramInlineKeyboardRows.add(telegramInlineKeyboardRow)
    }
    telegramInlineKeyboardMarkup.keyboard = telegramInlineKeyboardRows
    telegramInlineKeyboardMarkup
}
