package io.chengine.command

import io.chengine.connector.BotApiIdentifier
import io.chengine.connector.BotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.connector.get
import io.chengine.getUserId
import org.apache.logging.log4j.kotlin.logger
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramCommandRequestExtractor : CommandRequestExtractor {

    private val logger = logger()

    override fun support(): BotApiIdentifier {
        return TelegramBotApiIdentifier.instance
    }

    override fun extractFrom(request: BotRequestContext): Command? {
        return request.get<Update>()?.let {
            var command: String? = null
            if (it.hasCallbackQuery()) {
                command = it.callbackQuery.data
            }
            if (it.hasMessage()) {
                command = it.message.text
            }
            command?.let { c ->
                val validator = DefaultCommandValidator.instance
                if (validator.isCommand(c)) {
                    try {
                        DefaultCommandParser.instance.parse(c)
                    } catch (ex: CommandValidationException) {
                        logger.error(ex) { "Can not parse a command $c, userId: ${it.getUserId()}" }
                        return null
                    }
                } else {
                    logger.debug { "Received text $c not a command, userId: ${it.getUserId()}" }
                    return null
                }
            }
        }
    }
}