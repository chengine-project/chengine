package io.chengine

import io.chengine.connector.AbstractBot
import org.apache.logging.log4j.kotlin.logger
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.Serializable

open class TelegramBotsLongPoolingBot(
    val token: String,
    val userName: String,
    val abstractBot: AbstractBot
) : TelegramLongPollingBot() {

    private val logger = logger()

    init {
        logger.info { "Initializing telegram long pooling bot" }
    }

    override fun getBotToken(): String = token

    override fun getBotUsername(): String = userName

    override fun onUpdateReceived(update: Update) {
        logger.info { "Update received, userId: ${update.message.from.id}, chatId: ${update.message.chatId}" }
        abstractBot.process(update)
    }

    override fun <T : Serializable, Method : BotApiMethod<T>> execute(method: Method): T = super.execute(method)
}