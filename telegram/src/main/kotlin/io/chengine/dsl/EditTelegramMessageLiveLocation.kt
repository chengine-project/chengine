package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageLiveLocation

fun editTelegramMessageLiveLocation(block: EditTelegramMessageLiveLocationBuilder.() -> Unit): EditMessageLiveLocation {
    return EditTelegramMessageLiveLocationBuilder().apply(block)._build()
}

class EditTelegramMessageLiveLocationBuilder {
    fun _build(): EditMessageLiveLocation {
        val editMessageLiveLocation = EditMessageLiveLocation()

        return editMessageLiveLocation
    }
}