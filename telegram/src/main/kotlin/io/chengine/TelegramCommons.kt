package io.chengine

import io.chengine.connector.BotRequestContext
import io.chengine.connector.get
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

fun BotRequestContext.getTelegramUser(): User? = get<Update>()?.message?.from

fun BotRequestContext.getTelegramChat(): Chat? = get<Update>()?.message?.chat

fun BotRequestContext.getTelegramChatId(): String = getTelegramChat()?.id.toString()