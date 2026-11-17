package io.chengine

import io.chengine.connector.AbstractBot
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient

/** Connects Telegram's update consumer and client to Chengine. */
open class TelegramBotsLongPoolingBot(
    val token: String,
    val userName: String,
    val abstractBot: AbstractBot,
    val client: TelegramClient = OkHttpTelegramClient(token)
) : LongPollingUpdateConsumer {
    override fun consume(updates: List<Update>) {
        updates.forEach { abstractBot.process(it) }
    }
}
