---
layout: default
lang: de
title: Fotos, Dateien und Medien
section: FUNKTIONEN
permalink: /de/media/
lead: Fotos, Dokumente, Video, Audio und weitere Medien senden und empfangen.
---

## Lokale Datei

```kotlin
import io.chengine.dsl.sendTelegramPhoto
import java.io.File

@HandleCommand("/photo")
fun photo() = sendTelegramPhoto {
    photo { file = File("photo.jpg") }
    caption = "Foto aus einer Datei"
}
```

Ohne explizite Angabe wird `chatId` aus der eingehenden Nachricht übernommen. `sendTelegramDocument`, `sendTelegramVideo` und `sendTelegramAudio` funktionieren analog.

```kotlin
@HandleCommand("/document")
fun document() = sendTelegramDocument {
    document { file = File("document.pdf") }
    caption = "Dokument"
}
```

## Datei-ID und Stream

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
    title = "Aufnahme"
}
```

Ohne Datei oder Stream bezeichnet `name` eine Telegram-`file_id` oder URL. Ein Stream benötigt einen Namen und kann nach Verbrauch nicht erneut gesendet werden. Ein `InputFile` kann auch direkt an `photo`, `document`, `video` oder `audio` übergeben werden.

Auch `sendTelegramSticker`, `sendTelegramAnimation` und `sendTelegramVideoNote` stehen zur Verfügung. Video unterstützt Abmessungen und Dauer, Audio Titel und Interpret. Für Videonachrichten verlangt Telegram eine geeignete quadratische Datei.

## Eingehende Medien

```kotlin
@TelegramHandleMedia
fun media(update: Update) = sendTelegramMessage {
    text = if (update.message.photo != null) "Foto empfangen" else "Datei empfangen"
}
```

Das ursprüngliche `Update` enthält Foto, Dokument, Video, Audio, Sprachnachricht, Animation, Videonachricht oder Sticker; die Bildunterschrift steht in `update.message.caption`. Zum Herunterladen verwende die Telegram-API und die `fileId`.

## Bildunterschrift und Medien bearbeiten

`editTelegramMessageCaption` ändert die Unterschrift; `editTelegramMedia` ersetzt das Medium:

```kotlin
editTelegramMedia {
    messageId = update.callbackQuery.message.messageId
    media {
        type = TelegramMediaType.PHOTO
        file = File("replacement.jpg")
        caption = "Neues Foto"
    }
}
```

`chatId` kann angegeben oder aus dem eingehenden Ereignis übernommen werden. Für Inline-Nachrichten verwende `inlineMessageId`: Telegram akzeptiert bei solchen Änderungen nur eine `file_id` oder URL, keinen neuen Upload. Außerdem beschränkt Telegram die Bearbeitung bestimmter Nachrichten und Mediengruppen.

[Vollständiges Beispiel im Repository](https://github.com/chengine-project/chengine/tree/develop/examples/media)
