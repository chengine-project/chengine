package io.chengine.command

import io.chengine.connector.BotApiIdentifier
import io.chengine.connector.BotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramCommandRequestExtractor : CommandRequestExtractor {

    override fun support(): BotApiIdentifier {
        return TelegramBotApiIdentifier.instance
    }

    override fun extractFrom(request: BotRequestContext): Command? {
        return request.get(Update::class)?.let {
            var command: String? = null
            if (it.hasCallbackQuery()) {
                command = it.callbackQuery.data
            }
            if (it.hasMessage()) {
                command = it.message.text
            }
            command?.let { c ->
                DefaultCommandParser.instance.parse(c)
            }
        }
    }
}