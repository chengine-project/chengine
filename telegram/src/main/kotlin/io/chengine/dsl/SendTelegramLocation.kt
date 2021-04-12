package io.chengine.dsl

import io.chengine.common.isNull
import org.telegram.telegrambots.meta.api.methods.send.SendLocation

fun sendTelegramLocation(block: SendTelegramLocationBuilder.() -> Unit) =
    SendTelegramLocationBuilder().apply(block)._build()

class SendTelegramLocationBuilder(
    override var inlineKeyboard: TelegramInlineKeyboard? = null
) : InlineKeyboardSupporter {

    var chatId: String? = null
    var allowSendingWithoutReply: Boolean? = null
    var disableNotification: Boolean? = null
    var heading: Int? = null
    var horizontalAccuracy: Double? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var livePeriod: Int? = null
    var proximityAlertRadius: Int? = null
    var replyToMessageId: Int? = null

    fun _build(): SendLocation {
        latitude.isNull {
            throw RuntimeException("Location latitude can't be null")
        }
        longitude.isNull {
            throw RuntimeException("Location longitude can't be null")
        }

        val sendLocation = SendLocation()
        chatId?.let {
            sendLocation.chatId = it
        }
        sendLocation.allowSendingWithoutReply = allowSendingWithoutReply
        sendLocation.disableNotification = disableNotification
        sendLocation.heading = heading
        sendLocation.horizontalAccuracy = horizontalAccuracy
        sendLocation.latitude = latitude!!
        sendLocation.longitude = longitude!!
        sendLocation.livePeriod = livePeriod
        sendLocation.proximityAlertRadius = proximityAlertRadius
        sendLocation.replyToMessageId = replyToMessageId
        sendLocation.replyMarkup = inlineKeyboard.toReplyMarkup()

        return sendLocation
    }
}