package io.chengine

import io.chengine.connector.BotRequestContext
import io.chengine.connector.get
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

fun BotRequestContext.getTelegramUser(): User? = get<Update>()?.message?.from

fun BotRequestContext.getTelegramChat(): Chat? = get<Update>()?.message?.chat

fun BotRequestContext.getTelegramChatId(): String = get<Update>()?.getChatId().toString()

fun Update.getUserId(): Long? {
    val fromMessage: Long? = this.message?.from?.id
    val fromCallback: Long? = this.callbackQuery?.from?.id
    val fromPreCheckoutQuery: Long? = this.preCheckoutQuery?.from?.id
    return fromMessage ?: fromCallback ?: fromPreCheckoutQuery
}

fun Update.getChatId(): Long? {
    val fromMessage: Long? = this.message?.chatId
    val fromCallback: Long? = this.callbackQuery?.message?.chatId
    return fromMessage ?: fromCallback
}