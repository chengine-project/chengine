package example.media

import io.chengine.command.HandleCommand
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
        caption = "Фото из файла"
    }

    @HandleCommand("/document") fun document() = sendTelegramDocument {
        document { file = File("document.pdf") }
        caption = "Документ из файла"
    }
}
