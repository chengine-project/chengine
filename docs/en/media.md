---
layout: default
lang: en
title: Photos, files and media
section: FEATURES
permalink: /en/media/
lead: Send and receive photos, documents, video, audio, and other media.
---

## Local file

```kotlin
import io.chengine.dsl.sendTelegramPhoto
import java.io.File

@HandleCommand("/photo")
fun photo() = sendTelegramPhoto {
    photo { file = File("photo.jpg") }
    caption = "Photo from file"
}
```

`chatId` is taken from the incoming message unless explicitly provided. `sendTelegramDocument`, `sendTelegramVideo`, and `sendTelegramAudio` work similarly.

```kotlin
@HandleCommand("/document")
fun document() = sendTelegramDocument {
    document { file = File("document.pdf") }
    caption = "Document"
}
```

## File ID and stream

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
    title = "Recording"
}
```

Without a file or stream, `name` is a Telegram `file_id` or URL. A stream requires a name and cannot be sent again after it has been consumed. You can also pass an `InputFile` directly to `photo`, `document`, `video`, or `audio`.

`sendTelegramSticker`, `sendTelegramAnimation`, and `sendTelegramVideoNote` are also available. Video supports dimensions and duration; audio supports title and performer. Telegram requires a suitable square file for video notes.

## Incoming media

```kotlin
@TelegramHandleMedia
fun media(update: Update) = sendTelegramMessage {
    text = if (update.message.photo != null) "Photo received" else "File received"
}
```

The original `Update` contains the photo, document, video, audio, voice, animation, video note, or sticker; the caption is in `update.message.caption`. Use the Telegram API and `fileId` to download a file.

## Editing captions and media

`editTelegramMessageCaption` changes a caption; `editTelegramMedia` replaces media:

```kotlin
editTelegramMedia {
    messageId = update.callbackQuery.message.messageId
    media {
        type = TelegramMediaType.PHOTO
        file = File("replacement.jpg")
        caption = "New photo"
    }
}
```

Provide `chatId` or let Chengine infer it from the incoming event. For inline messages, set `inlineMessageId`: Telegram accepts only a `file_id` or URL for such edits, not a new upload. Telegram also restricts edits to certain messages and media groups.

[Complete example]({{ '/en/examples/#media' | relative_url }})
