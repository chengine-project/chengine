package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Import
import org.springframework.context.ApplicationContext
import org.springframework.util.ClassUtils
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient
import org.telegram.telegrambots.meta.generics.TelegramClient

@Configuration
@AutoConfigureBefore(ChengineAutoConfiguration::class)
@Import(TelegramWebhookConfiguration::class)
open class ChengineTelegramAutoConfiguration(
    @Value("\${chengine.telegram.token:}") val token: String,
    @Value("\${chengine.telegram.username:}") val username: String,
    @Value("\${chengine.telegram.mode:polling}") val mode: String,
    context: ApplicationContext,
) {
    init {
        require(token.isNotBlank()) { "Set chengine.telegram.token to the Telegram bot token" }
        require(username.isNotBlank()) { "Set chengine.telegram.username to the Telegram bot username" }
        require(mode in setOf("polling", "webhook")) { "chengine.telegram.mode must be polling or webhook" }
        if (mode == "webhook") {
            val loader = context.classLoader
            require(ClassUtils.isPresent("org.springframework.web.servlet.DispatcherServlet", loader)) { "Webhook mode requires spring-boot-starter-web" }
            val webContext = ClassUtils.forName("org.springframework.web.context.WebApplicationContext", loader)
            require(webContext.isInstance(context)) { "Webhook mode requires a servlet web application" }
        }
    }
    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "chengine.telegram", name = ["mode"], havingValue = "polling", matchIfMissing = true)
    open fun telegramBotsApplication() = TelegramBotsLongPollingApplication()

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "chengine.telegram", name = ["mode"], havingValue = "polling", matchIfMissing = true)
    open fun telegramBotInitializer(application: TelegramBotsLongPollingApplication, bots: ObjectProvider<List<TelegramBotsLongPoolingBot>>, chengine: Chengine) =
        TelegramBotInitializer(application, bots.getIfAvailable { emptyList() })

    @Bean
    open fun longPollingBot(chengineBot: ChengineTelegramLongPoolingBot): TelegramBotsLongPoolingBot =
        chengineBot.telegramBotsLongPollingBotBot

    @Bean
    open fun chengineBot(clients: ObjectProvider<TelegramClient>) = ChengineTelegramLongPoolingBot().also {
        it.telegramBotsLongPollingBotBot = TelegramBotsLongPoolingBot(token, username, it, clients.getIfAvailable { OkHttpTelegramClient(token) })
    }
}
