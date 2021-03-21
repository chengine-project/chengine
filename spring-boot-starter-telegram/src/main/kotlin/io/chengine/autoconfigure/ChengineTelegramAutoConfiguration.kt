package io.chengine.autoconfigure

import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import org.apache.logging.log4j.kotlin.logger
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.meta.generics.LongPollingBot
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
@AutoConfigureBefore(ChengineAutoConfiguration::class)
open class ChengineTelegramAutoConfiguration(
    @Value("\${chengine.telegram.token}") val token: String,
    @Value("\${chengine.telegram.username}") val username: String,
) {

    @Bean
    @ConditionalOnMissingBean(TelegramBotsApi::class)
    @Throws(TelegramApiException::class)
    open fun telegramBotsApi(): TelegramBotsApi = TelegramBotsApi(DefaultBotSession::class.java)

    @Bean
    @ConditionalOnMissingBean
    open fun telegramBotInitializer(
        telegramBotsApi: TelegramBotsApi,
        longPollingBots: ObjectProvider<List<LongPollingBot>>
    ): TelegramBotInitializer {
        return TelegramBotInitializer(telegramBotsApi, longPollingBots.getIfAvailable { emptyList() })
    }

    @Bean
    open fun longPollingBot(chengineBot: ChengineTelegramLongPoolingBot): TelegramLongPollingBot {
        return TelegramBotsLongPoolingBot(
            token = token,
            userName = username,
            abstractBot = chengineBot
        ).also {
            chengineBot.telegramBotsLongPollingBotBot = it
        }
    }

    @Bean
    open fun chengineBot(): ChengineTelegramLongPoolingBot = ChengineTelegramLongPoolingBot()

}