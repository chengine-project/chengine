package io.chengine

import io.chengine.connector.BotRequestContext
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

fun BotRequestContext.getTelegramUser(): User? = get(Update::class)?.message?.from

fun BotRequestContext.getTelegramChat(): Chat? = get(Update::class)?.message?.chat

fun BotRequestContext.getTelegramChatId(): String = getTelegramChat()?.id.toString()