package io.chengine

import io.chengine.connector.BotRequestContext
import io.chengine.connector.Bot
import io.chengine.poll.TelegramPollOrigin
import io.chengine.connector.get
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

fun BotRequestContext.getTelegramUser(): User? = get<Update>()?.message?.from

fun BotRequestContext.getTelegramChat(): Chat? = get<Update>()?.message?.chat

/** Original poll location when this bot previously sent the poll and the mapping is available. */
fun BotRequestContext.telegramPollOrigin(): TelegramPollOrigin? {
    val update = get<Update>() ?: return null
    val pollId = update.pollAnswer?.pollId ?: update.poll?.id ?: return null
    val bot = get<Bot>() as? ChengineTelegramLongPoolingBot ?: return null
    return bot.pollOriginStore.get(bot.sessionBotIdentifier(), pollId)
}

fun BotRequestContext.getTelegramChatId(): String =
    (get<Update>()?.getChatId() ?: telegramPollOrigin()?.chatId)?.toString()
        ?: throw IllegalStateException("Cannot determine Telegram chat ID for this update")

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