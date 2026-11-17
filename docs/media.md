---
layout: default
title: Фото, файлы и медиа
section: ВОЗМОЖНОСТИ
permalink: /media/
lead: Отправляйте и принимайте фото, документы, видео, аудио и другие медиа.
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

Также доступны `sendTelegramSticker`, `sendTelegramAnimation` и `sendTelegramVideoNote`. Видео поддерживает размеры и длительность, аудио — название и исполнителя. Для видеосообщения Telegram требует подходящий квадратный файл.

## Входящие медиа

```kotlin
@TelegramHandleMedia
fun media(update: Update) = sendTelegramMessage {
    text = if (update.message.photo != null) "Получено фото" else "Получен файл"
}
```

В исходном `Update` доступны фото, документ, видео, аудио, голосовое, анимация, видеосообщение или стикер; подпись находится в `update.message.caption`. Для загрузки файла используйте Telegram API и его `fileId`.

## Изменение подписи и файла

`editTelegramMessageCaption` меняет подпись, а `editTelegramMedia` заменяет медиа:

```kotlin
editTelegramMedia {
    messageId = update.callbackQuery.message.messageId
    media {
        type = TelegramMediaType.PHOTO
        file = File("replacement.jpg")
        caption = "Новое фото"
    }
}
```

Можно задать `chatId` или позволить Chengine взять его из входящего события. Для inline-сообщения задайте `inlineMessageId`: при таком редактировании Telegram принимает только `file_id` или URL, а не новый файл. Telegram также ограничивает редактирование некоторых сообщений и медиагрупп.

[Полный пример в репозитории](https://github.com/chengine-project/chengine/tree/develop/examples/media)
