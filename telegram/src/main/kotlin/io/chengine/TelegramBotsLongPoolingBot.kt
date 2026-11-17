package io.chengine

import io.chengine.connector.AbstractBot
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.generics.TelegramClient
import org.apache.logging.log4j.LogManager
import java.lang.reflect.InvocationTargetException
import java.util.concurrent.CancellationException

/** Connects Telegram's update consumer and client to Chengine. */
open class TelegramBotsLongPoolingBot(
    val token: String,
    val userName: String,
    val abstractBot: AbstractBot,
    val client: TelegramClient = OkHttpTelegramClient(token)
) : LongPollingUpdateConsumer {
    private val logger = LogManager.getLogger(TelegramBotsLongPoolingBot::class.java)

    override fun consume(updates: List<Update>) {
        updates.forEach { update ->
            try {
                abstractBot.process(update)
            } catch (exception: Exception) {
                val cause = if (exception is InvocationTargetException) exception.targetException else exception
                when (cause) {
                    is CancellationException -> throw cause
                    is InterruptedException -> { Thread.currentThread().interrupt(); throw cause }
                    is Error -> throw cause
                    else -> onUpdateError(update, exception)
                }
            }
        }
    }

    /** Override to report failed updates to the application. Throw to stop batch processing. */
    protected open fun onUpdateError(update: Update, exception: Exception) {
        logger.error("Failed to process Telegram update {}", update.updateId, exception)
    }
}
