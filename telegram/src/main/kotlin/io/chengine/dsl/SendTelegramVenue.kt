package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.send.SendVenue

fun sendTelegramVenue(block: TelegramVenueBuilder.() -> Unit): SendVenue = TelegramVenueBuilder().apply(block)._build()


class TelegramVenueBuilder {

    var chatId: Long? = null
    var latitude: Double? = null
    var longitude: Double? = null
    var title: String? = null
    var disableNotification: Boolean? = null
    var address: String? = null
    var foursquareId: String? = null
    var replyToMessageId: String? = null

    fun _build(): SendVenue {
        return SendVenue().apply {

        }
    }
}

//private static final String CHATID_FIELD = "chat_id";
//private static final String LATITUDE_FIELD = "latitude";
//private static final String LONGITUDE_FIELD = "longitude";
//private static final String TITLE_FIELD = "title";
//private static final String DISABLENOTIFICATION_FIELD = "disable_notification";
//private static final String ADDRESS_FIELD = "address";
//private static final String FOURSQUAREID_FIELD = "foursquare_id";
//private static final String REPLYTOMESSAGEID_FIELD = "reply_to_message_id";
//private static final String REPLYMARKUP_FIELD = "reply_markup";
//private static final String FOURSQUARETYPE_FIELD = "foursquare_type";
//private static final String ALLOWSENDINGWITHOUTREPLY_FIELD = "allow_sending_without_reply";
//private static final String GOOGLEPLACEID_FIELD = "google_place_id";
//private static final String GOOGLEPLACETYPE_FIELD = "google_place_type";