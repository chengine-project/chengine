package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication

@Configuration
@AutoConfigureBefore(ChengineAutoConfiguration::class)
open class ChengineTelegramAutoConfiguration(
    @Value("\${chengine.telegram.token}") val token: String,
    @Value("\${chengine.telegram.username}") val username: String,
) {
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    open fun telegramBotsApplication() = TelegramBotsLongPollingApplication()

    @Bean
    @ConditionalOnMissingBean
    open fun telegramBotInitializer(application: TelegramBotsLongPollingApplication, bots: ObjectProvider<List<TelegramBotsLongPoolingBot>>, chengine: Chengine) =
        TelegramBotInitializer(application, bots.getIfAvailable { emptyList() })

    @Bean
    open fun longPollingBot(chengineBot: ChengineTelegramLongPoolingBot): TelegramBotsLongPoolingBot =
        TelegramBotsLongPoolingBot(token, username, chengineBot).also { chengineBot.telegramBotsLongPollingBotBot = it }

    @Bean
    open fun chengineBot() = ChengineTelegramLongPoolingBot()
}
