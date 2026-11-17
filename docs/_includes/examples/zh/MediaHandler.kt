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
        caption = "来自文件的照片"
    }

    @HandleCommand("/document") fun document() = sendTelegramDocument {
        document { file = File("document.pdf") }
        caption = "来自文件的文档"
    }
    @HandleCommand("/video") fun video() = sendTelegramVideo {
        video { file = File("video.mp4") }
        caption = "来自文件的视频"
        supportsStreaming = true
    }

    @HandleCommand("/audio") fun audio() = sendTelegramAudio {
        audio { file = File("audio.mp3") }
        title = "音频示例"
    }
}
