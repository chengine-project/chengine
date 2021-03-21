@file:Suppress("WARNINGS")

package io.chengine

import io.chengine.common.isNull
import io.chengine.connector.BotRequestContext
import org.telegram.telegrambots.meta.api.methods.games.SetGameScore
import org.telegram.telegrambots.meta.api.methods.groupadministration.*
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.polls.StopPoll
import org.telegram.telegrambots.meta.api.methods.send.*
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*

inline fun SendMessage.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendInvoice.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId().toInt()
}

inline fun SendDocument.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendLocation.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendDice.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendContact.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendPhoto.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendMediaGroup.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendAnimation.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendAudio.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendChatAction.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendGame.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendPoll.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun StopPoll.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendVenue.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendVideo.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendVoice.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendVideoNote.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SendSticker.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun EditMessageCaption.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun EditMessageMedia.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun EditMessageLiveLocation.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun EditMessageReplyMarkup.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun EditMessageText.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun DeleteMessage.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

// Set group ************************************************************************************

inline fun SetChatAdministratorCustomTitle.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SetChatDescription.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SetChatPermissions.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SetChatPhoto.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SetChatStickerSet.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SetChatTitle.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}

inline fun SetGameScore.setChatIdFromRequest(requestContext: BotRequestContext) = chatId.isNull {
    chatId = requestContext.getTelegramChatId()
}
