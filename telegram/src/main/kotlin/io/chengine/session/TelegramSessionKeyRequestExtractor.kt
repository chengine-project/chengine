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

    override fun extractFrom(request: BotRequestContext): SessionKey {
        return request.get<Update>()?.let {
            val userId = it.getUserId().toString()
            val chatId = it.getChatId().toString()
            SessionKey(userId, chatId, support().identifier())
        } ?: throw RuntimeException("Can't extract session message key")
    }
}