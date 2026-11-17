package io.chengine.session

import io.chengine.connector.BotApiIdentifier
import io.chengine.connector.BotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.connector.get
import io.chengine.getChatId
import io.chengine.getUserId
import org.telegram.telegrambots.meta.api.objects.Update

class TelegramSessionKeyRequestExtractor : SessionKeyRequestExtractor {

    override fun support(): BotApiIdentifier {
        return TelegramBotApiIdentifier.instance
    }

    override fun extractFrom(request: BotRequestContext): SessionKey? {
        val update = request.get<Update>() ?: return null
        val userId = update.getUserId() ?: return null
        val chatId = update.getChatId() ?: return null
        return SessionKey(userId.toString(), chatId.toString(), support().identifier())
    }
}
