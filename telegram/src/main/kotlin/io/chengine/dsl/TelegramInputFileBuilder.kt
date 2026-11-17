package io.chengine.dsl

import org.telegram.telegrambots.meta.api.objects.InputFile
import java.io.File
import java.io.InputStream

/** Set name alone to use a Telegram file_id or URL; for uploads use file or a named stream. */
open class TelegramInputFileBuilder {
    var name: String? = null
    var file: File? = null
    var mediaStream: InputStream? = null

    fun _build(): InputFile {
        require(name == null || name!!.isNotBlank()) { "File name or identifier must not be blank" }
        require(file == null || mediaStream == null) { "Choose either a file or a stream" }
        return when {
            file != null && name != null -> InputFile(file, name)
            file != null -> InputFile(file)
            mediaStream != null && name != null -> InputFile(mediaStream, name)
            mediaStream != null -> throw IllegalArgumentException("A stream requires a file name")
            name != null -> InputFile(name)
            else -> throw IllegalArgumentException("A file, stream, or Telegram file_id is required")
        }
    }
}
