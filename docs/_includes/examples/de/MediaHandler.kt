package example.media

import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramAudio
import io.chengine.dsl.sendTelegramVideo
import io.chengine.dsl.sendTelegramDocument
import io.chengine.dsl.sendTelegramPhoto
import io.chengine.handler.Handler
import org.springframework.stereotype.Component
import java.io.File

/** Stelle die Dateien und Datei-IDs für deine Anwendung bereit. */
@Handler
@Component
class MediaHandler {
    @HandleCommand("/photo") fun photo() = sendTelegramPhoto {
        photo { file = File("photo.jpg") }
        caption = "Foto aus einer Datei"
    }

    @HandleCommand("/document") fun document() = sendTelegramDocument {
        document { file = File("document.pdf") }
        caption = "Dokument aus einer Datei"
    }
    @HandleCommand("/video") fun video() = sendTelegramVideo {
        video { file = File("video.mp4") }
        caption = "Video aus einer Datei"
        supportsStreaming = true
    }

    @HandleCommand("/audio") fun audio() = sendTelegramAudio {
        audio { file = File("audio.mp3") }
        title = "Audiobeispiel"
    }
}
