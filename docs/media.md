---
layout: default
title: Фото, файлы и медиа
section: ВОЗМОЖНОСТИ
permalink: /media/
lead: Отправляйте фото, документы, видео и аудио из файла, потока или по Telegram file_id.
---

## Файл с диска

```kotlin
import io.chengine.dsl.sendTelegramPhoto
import java.io.File

@HandleCommand("/photo")
fun photo() = sendTelegramPhoto {
    photo { file = File("photo.jpg") }
    caption = "Фото из файла"
}
```

`chatId` подставляется из входящего сообщения, если не задан явно. Аналогично работают `sendTelegramDocument`, `sendTelegramVideo` и `sendTelegramAudio`.

```kotlin
@HandleCommand("/document")
fun document() = sendTelegramDocument {
    document { file = File("document.pdf") }
    caption = "Документ"
}
```

## File ID и поток

```kotlin
val message = sendTelegramVideo {
    video { name = "TELEGRAM_FILE_ID" }
    supportsStreaming = true
}

val audioMessage = sendTelegramAudio {
    audio {
        mediaStream = inputStream
        name = "audio.mp3"
    }
    title = "Запись"
}
```

`name` без файла или потока означает Telegram `file_id` или URL. Для потока имя обязательно; использованный поток нельзя отправить повторно. Можно также передать `InputFile` напрямую в свойство `photo`, `document`, `video` или `audio`.

Видео поддерживает размеры и длительность, аудио — название, исполнителя и длительность. Некоторые другие DSL-методы ещё не реализованы — сверяйтесь с исходным кодом и примерами.

[Полный пример в репозитории](https://github.com/chengine-project/chengine/tree/develop/examples/media)
