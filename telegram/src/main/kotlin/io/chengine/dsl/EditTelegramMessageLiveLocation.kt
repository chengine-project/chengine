package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageLiveLocation

fun editTelegramMessageLiveLocation(block: EditTelegramMessageLiveLocationBuilder.() -> Unit): EditMessageLiveLocation =
    EditTelegramMessageLiveLocationBuilder().apply(block)._build()

class EditTelegramMessageLiveLocationBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: Long? = null
    var messageId: Int? = null
    var inlineMessageId: String? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var horizontalAccuracy: Double? = null
    var heading: Int? = null
    var proximityAlertRadius: Int? = null
    fun _build(): EditMessageLiveLocation {
        validateEditTarget(chatId, messageId, inlineMessageId)
        val lat = requireNotNull(latitude) { "Latitude is required" }
        val lon = requireNotNull(longitude) { "Longitude is required" }
        require(lat in -90.0..90.0 && lon in -180.0..180.0) { "Invalid coordinates" }
        return EditMessageLiveLocation.builder().latitude(lat).longitude(lon).build().apply {
            chatId = this@EditTelegramMessageLiveLocationBuilder.chatId?.toString()
            messageId = this@EditTelegramMessageLiveLocationBuilder.messageId
            inlineMessageId = this@EditTelegramMessageLiveLocationBuilder.inlineMessageId
            horizontalAccuracy = this@EditTelegramMessageLiveLocationBuilder.horizontalAccuracy
            heading = this@EditTelegramMessageLiveLocationBuilder.heading
            proximityAlertRadius = this@EditTelegramMessageLiveLocationBuilder.proximityAlertRadius
            replyMarkup = inlineKeyboard.toReplyMarkup()
        }
    }
}
