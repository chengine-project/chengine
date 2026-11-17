package example.media

import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramAudio
import io.chengine.dsl.sendTelegramVideo
import io.chengine.dsl.sendTelegramDocument
import io.chengine.dsl.sendTelegramPhoto
import io.chengine.handler.Handler
import org.springframework.stereotype.Component
import java.io.File

/** Supply the files and file IDs for your application. */
@Handler
@Component
class MediaHandler {
    @HandleCommand("/photo") fun photo() = sendTelegramPhoto {
        photo { file = File("photo.jpg") }
        caption = "Photo from a file"
    }

    @HandleCommand("/document") fun document() = sendTelegramDocument {
        document { file = File("document.pdf") }
        caption = "Document from a file"
    }
    @HandleCommand("/video") fun video() = sendTelegramVideo {
        video { file = File("video.mp4") }
        caption = "Video from a file"
        supportsStreaming = true
    }

    @HandleCommand("/audio") fun audio() = sendTelegramAudio {
        audio { file = File("audio.mp3") }
        title = "Audio example"
    }
}
