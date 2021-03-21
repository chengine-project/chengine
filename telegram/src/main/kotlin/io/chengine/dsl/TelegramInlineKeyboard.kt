package io.chengine.dsl

import io.chengine.common.isNull
import io.chengine.common.then
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

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
    val callbackData: String,
    val url: String?,
    val switchInlineQuery: String?,
    val switchInlineQueryCurrentChat: String?,
    val pay: Boolean?
)

// Builders ******************************************************************************************************

@SendTelegramMessageDsl
@TelegramInlineKeyboardDsl
class TelegramInlineKeyboardBuilder {
    private val rows: ArrayList<TelegramInlineKeyboardRow> = ArrayList()
    fun _build(): TelegramInlineKeyboard {
        TODO()
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
        TODO()
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

    fun _build(): TelegramInlineKeyboardButton {
        text.isNull().then {
            throw RuntimeException("Button text can't be null")
        }
        callbackData.isNull().then {
            throw RuntimeException("Button callback data can't be null")
        }

        return TelegramInlineKeyboardButton(
            text!!,
            callbackData!!,
            url,
            switchInlineQuery,
            switchInlineQueryCurrentChat,
            pay
        )
    }
}

// ************************************************************************************************************

interface InlineKeyboardSupporter {
    var inlineKeyboard: TelegramInlineKeyboard?
    fun inlineKeyboard(block: TelegramInlineKeyboardBuilder.() -> Unit) {
        inlineKeyboard = TelegramInlineKeyboardBuilder().apply(block)._build()
    }
}

// Converter

fun TelegramInlineKeyboard?.toReplyMarkup(): ReplyKeyboardMarkup? {
    TODO()
}