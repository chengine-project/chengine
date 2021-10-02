package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.connector.BotResponseContext
import io.chengine.connector.DefaultBotResponseContext
import io.chengine.setChatIdFromRequest
import org.telegram.telegrambots.meta.api.methods.*
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands
import org.telegram.telegrambots.meta.api.methods.games.SetGameScore
import org.telegram.telegrambots.meta.api.methods.groupadministration.*
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.methods.polls.StopPoll
import org.telegram.telegrambots.meta.api.methods.send.*
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*
import java.io.Serializable
import kotlin.reflect.KClass

class PartialApiMethodReturnValueHandler : AbstractMethodReturnValueHandler<PartialBotApiMethod<out Serializable>>() {

    override fun support(): KClass<out PartialBotApiMethod<out Serializable>> = PartialBotApiMethod::class

    override fun process(
        returnedObject: Any,
        handlerMethod: HandlerMethod,
        requestContext: BotRequestContext,
        responseContext: BotResponseContext
    ) {
        when (returnedObject) {
            // Send Group
            is SendMessage -> returnedObject.setChatIdFromRequest(requestContext)
            is SendInvoice -> returnedObject.setChatIdFromRequest(requestContext)
            is SendDocument -> returnedObject.setChatIdFromRequest(requestContext)
            is SendLocation -> returnedObject.setChatIdFromRequest(requestContext)
            is SendDice -> returnedObject.setChatIdFromRequest(requestContext)
            is SendContact -> returnedObject.setChatIdFromRequest(requestContext)
            is SendPhoto -> returnedObject.setChatIdFromRequest(requestContext)
            is SendMediaGroup -> returnedObject.setChatIdFromRequest(requestContext)
            is SendAnimation -> returnedObject.setChatIdFromRequest(requestContext)
            is SendAudio -> returnedObject.setChatIdFromRequest(requestContext)
            is SendChatAction -> returnedObject.setChatIdFromRequest(requestContext)
            is SendGame -> returnedObject.setChatIdFromRequest(requestContext)
            is SendVideoNote -> returnedObject.setChatIdFromRequest(requestContext)
            is SendVenue -> returnedObject.setChatIdFromRequest(requestContext)
            is SendVideo -> returnedObject.setChatIdFromRequest(requestContext)
            is SendVoice -> returnedObject.setChatIdFromRequest(requestContext)
            is SendSticker -> returnedObject.setChatIdFromRequest(requestContext)
            is SendPoll -> returnedObject.setChatIdFromRequest(requestContext)
            is StopPoll -> returnedObject.setChatIdFromRequest(requestContext)
            // Edit group
            is EditMessageCaption -> returnedObject.setChatIdFromRequest(requestContext)
            is EditMessageMedia -> returnedObject.setChatIdFromRequest(requestContext)
            is EditMessageLiveLocation -> returnedObject.setChatIdFromRequest(requestContext)
            is EditMessageReplyMarkup -> returnedObject.setChatIdFromRequest(requestContext)
            is EditMessageText -> returnedObject.setChatIdFromRequest(requestContext)
            // Delete group
            is DeleteMessage -> returnedObject.setChatIdFromRequest(requestContext)
            // Set group
            is SetChatAdministratorCustomTitle -> returnedObject.setChatIdFromRequest(requestContext)
            is SetChatDescription -> returnedObject.setChatIdFromRequest(requestContext)
            is SetChatPermissions -> returnedObject.setChatIdFromRequest(requestContext)
            is SetChatPhoto -> returnedObject.setChatIdFromRequest(requestContext)
            is SetChatStickerSet -> returnedObject.setChatIdFromRequest(requestContext)
            is SetChatTitle -> returnedObject.setChatIdFromRequest(requestContext)
            is SetGameScore -> returnedObject.setChatIdFromRequest(requestContext)
            is SetMyCommands -> {}

            is AnswerPreCheckoutQuery -> {}
            is AnswerCallbackQuery -> {}
            is AnswerInlineQuery -> {}
            is AnswerShippingQuery -> {}
            else -> throw RuntimeException("Unsupported returned object type: ${returnedObject::class}")
        }

        (responseContext as DefaultBotResponseContext).currentResponseObject = returnedObject
    }
}