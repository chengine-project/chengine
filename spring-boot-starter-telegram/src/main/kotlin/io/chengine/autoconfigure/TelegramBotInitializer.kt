package io.chengine.autoconfigure

import org.springframework.beans.factory.InitializingBean
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot

open class TelegramBotInitializer(
    private val telegramBotsApi: TelegramBotsApi,
    private val longPollingBot: List<LongPollingBot>
) : InitializingBean {

    override fun afterPropertiesSet() {
        try {
            longPollingBot.forEach {
                telegramBotsApi.registerBot(it)
            }
        } catch (ex: TelegramApiException) {
            throw RuntimeException(ex)
        }
    }
}