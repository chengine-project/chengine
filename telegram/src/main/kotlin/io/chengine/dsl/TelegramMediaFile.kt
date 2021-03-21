package io.chengine.dsl

import java.io.File
import java.io.InputStream

class TelegramMediaFileBuilder {

    var attachName: String? = null
    var mediaName: String? = null
    var newMediaFile: File? = null
    var newMediaStream: InputStream? = null
    var isNew: Boolean? = null

    fun _build(): TelegramMediaFile {
        return TelegramMediaFile(
            attachName,
            mediaName,
            newMediaFile,
            newMediaStream,
            isNew
        )
    }
}

data class TelegramMediaFile(
    val attachName: String?,
    val mediaName: String?,
    val newMediaFile: File?,
    val newMediaStream: InputStream?,
    val isNew: Boolean?
)