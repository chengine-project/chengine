package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia

fun editTelegramMedia(block: EditTelegramMessageMediaBuilder.() -> Unit): EditMessageMedia {
    return EditTelegramMessageMediaBuilder().apply(block)._build()
}

class EditTelegramMessageMediaBuilder {



    fun _build(): EditMessageMedia {
        TODO()
    }

    fun media(block: MediaBuilder.() -> Unit) {

    }
}

class MediaBuilder {

}