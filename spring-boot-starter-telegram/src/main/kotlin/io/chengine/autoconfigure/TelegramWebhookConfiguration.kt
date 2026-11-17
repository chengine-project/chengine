package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineTelegramLongPoolingBot
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.telegram.telegrambots.meta.api.objects.Update
import java.security.MessageDigest

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(name = ["org.springframework.web.servlet.DispatcherServlet"])
@ConditionalOnProperty(prefix = "chengine.telegram", name = ["mode"], havingValue = "webhook")
open class TelegramWebhookConfiguration {
    @Bean
    open fun telegramWebhookController(
        bot: ChengineTelegramLongPoolingBot,
        chengine: Chengine,
        @Value("\${chengine.telegram.webhook.secret:}") secret: String
    ): TelegramWebhookController = TelegramWebhookController(bot, secret)
}

@RestController
class TelegramWebhookController(private val bot: ChengineTelegramLongPoolingBot, secret: String) {
    private val expectedSecret = secret.toByteArray(Charsets.UTF_8)

    init {
        require(secret.matches(Regex("[A-Za-z0-9_-]{1,256}"))) {
            "chengine.telegram.webhook.secret must contain 1-256 letters, digits, underscores or hyphens"
        }
    }

    @PostMapping("\${chengine.telegram.webhook.path:/telegram/webhook}")
    fun receive(
        @RequestHeader(value = "X-Telegram-Bot-Api-Secret-Token", required = false) secret: String?,
        @RequestBody update: Update
    ): ResponseEntity<Void> {
        if (secret == null || !MessageDigest.isEqual(expectedSecret, secret.toByteArray(Charsets.UTF_8))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        }
        bot.process(update)
        return ResponseEntity.ok().build()
    }
}
