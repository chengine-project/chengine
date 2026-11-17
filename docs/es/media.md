---
layout: default
lang: es
title: Fotos, archivos y multimedia
section: FUNCIONALIDADES
permalink: /es/media/
lead: Envía y recibe fotos, documentos, vídeos, audio y otros archivos multimedia.
---

## Archivo local

```kotlin
import io.chengine.dsl.sendTelegramPhoto
import java.io.File

@HandleCommand("/photo")
fun photo() = sendTelegramPhoto {
    photo { file = File("photo.jpg") }
    caption = "Foto desde archivo"
}
```

Si no se especifica, `chatId` se obtiene del mensaje entrante. `sendTelegramDocument`, `sendTelegramVideo` y `sendTelegramAudio` funcionan de forma similar.

```kotlin
@HandleCommand("/document")
fun document() = sendTelegramDocument {
    document { file = File("document.pdf") }
    caption = "Documento"
}
```

## ID de archivo y flujo

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
    title = "Grabación"
}
```

`name` sin archivo ni flujo significa un `file_id` de Telegram o una URL. El nombre es obligatorio para un flujo; un flujo consumido no puede enviarse de nuevo. También puedes pasar `InputFile` directamente a `photo`, `document`, `video` o `audio`.

También están disponibles `sendTelegramSticker`, `sendTelegramAnimation` y `sendTelegramVideoNote`. Los vídeos admiten tamaño y duración; el audio, título e intérprete. Telegram requiere un archivo cuadrado adecuado para los mensajes de vídeo.

## Multimedia entrante

```kotlin
@TelegramHandleMedia
fun media(update: Update) = sendTelegramMessage {
    text = if (update.message.photo != null) "Foto recibida" else "Archivo recibido"
}
```

El `Update` original puede contener una foto, documento, vídeo, audio, nota de voz, animación, mensaje de vídeo o sticker; la descripción está en `update.message.caption`. Para descargar el archivo, utiliza la API de Telegram y su `fileId`.

## Editar la descripción y el archivo

`editTelegramMessageCaption` cambia la descripción y `editTelegramMedia` reemplaza el archivo multimedia:

```kotlin
editTelegramMedia {
    messageId = update.callbackQuery.message.messageId
    media {
        type = TelegramMediaType.PHOTO
        file = File("replacement.jpg")
        caption = "Nueva foto"
    }
}
```

Puedes especificar `chatId` o dejar que Chengine lo obtenga del evento entrante. Para un mensaje inline usa `inlineMessageId`: Telegram solo acepta `file_id` o URL, no una nueva carga. Telegram también restringe la edición de ciertos mensajes y grupos multimedia.

[Ejemplo completo]({{ '/es/examples/#media' | relative_url }})
