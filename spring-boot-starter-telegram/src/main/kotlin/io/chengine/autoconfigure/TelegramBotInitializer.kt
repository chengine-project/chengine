package io.chengine.autoconfigure

import io.chengine.TelegramBotsLongPoolingBot
import org.springframework.beans.factory.InitializingBean
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

open class TelegramBotInitializer(
    private val application: TelegramBotsLongPollingApplication,
    private val bots: List<TelegramBotsLongPoolingBot>
) : InitializingBean {
    override fun afterPropertiesSet() { bots.forEach { application.registerBot(it.token, it) } }
}
