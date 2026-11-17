package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVenue

fun sendTelegramVenue(block: TelegramVenueBuilder.() -> Unit): SendVenue = TelegramVenueBuilder().apply(block)._build()

class TelegramVenueBuilder(override var inlineKeyboard: TelegramInlineKeyboard? = null) : InlineKeyboardSupporter {
    var chatId: Long? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var title: String? = null
    var address: String? = null
    var foursquareId: String? = null
    var disableNotification: Boolean? = null
    var replyToMessageId: String? = null
    fun _build(): SendVenue {
        val lat = requireNotNull(latitude) { "Latitude is required" }
        val lon = requireNotNull(longitude) { "Longitude is required" }
        require(lat in -90.0..90.0 && lon in -180.0..180.0) { "Invalid coordinates" }
        val venueTitle = requireNotNull(title) { "Title is required" }
        val venueAddress = requireNotNull(address) { "Address is required" }
        require(venueTitle.isNotBlank() && venueAddress.isNotBlank()) { "Title and address must not be blank" }
        val replyId = replyToMessageId?.let { requireNotNull(it.toIntOrNull()) { "Reply message ID must be an integer" } }
        return SendVenue.builder().chatId(chatId?.toString() ?: "").latitude(lat).longitude(lon).title(venueTitle).address(venueAddress).build().apply {
            foursquareId = this@TelegramVenueBuilder.foursquareId
            disableNotification = this@TelegramVenueBuilder.disableNotification
            replyToMessageId = replyId
            replyMarkup = inlineKeyboard.toReplyMarkup()
        }
    }
}
