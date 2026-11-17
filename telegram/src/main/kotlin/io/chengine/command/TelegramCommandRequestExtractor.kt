package io.chengine.command

import io.chengine.connector.BotApiIdentifier
import io.chengine.connector.BotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.connector.get
import org.apache.logging.log4j.kotlin.logger
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramCommandRequestExtractor(
    private val botUsername: () -> String? = { null }
) : CommandRequestExtractor {
    private val logger = logger()
    private val addressedCommand = Regex("^(/[^/#@\\s]+)@([a-zA-Z0-9_]+)(?=/|#|$)")

    override fun support(): BotApiIdentifier = TelegramBotApiIdentifier.instance

    override fun extractFrom(request: BotRequestContext): Command? {
        val update = request.get<Update>() ?: return null
        val messageText = update.message?.text
        var command = if (messageText != null) {
            messageText.trim().substringBefore(' ').substringBefore('\n').substringBefore('\t')
        } else {
            update.callbackQuery?.data
        } ?: return null

        if (!DefaultCommandValidator.instance.isCommand(command)) return null

        // Telegram addresses group commands to a specific bot. Callback data is Chengine's own format.
        if (messageText != null) {
            addressedCommand.find(command)?.let { match ->
                val username = botUsername() ?: return null
                if (!match.groupValues[2].equals(username.removePrefix("@"), ignoreCase = true)) return null
                command = match.groupValues[1] + command.substring(match.range.last + 1)
            }
        }
        return try {
            DefaultCommandParser.instance.parse(command)
        } catch (ex: CommandValidationException) {
            logger.debug { "Ignoring invalid command: ${ex.message}" }
            null
        } catch (ex: CommandParsingException) {
            logger.debug { "Ignoring invalid command: ${ex.message}" }
            null
        }
    }
}
