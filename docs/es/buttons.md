---
layout: default
lang: es
title: Botones y callbacks
section: FUNCIONALIDADES
permalink: /es/buttons/
lead: Añade un teclado inline a un mensaje y gestiona las pulsaciones.
---

## Envía un botón

```kotlin
@HandleCommand("/start")
fun start() = sendTelegramMessage {
    text = "Pulsa el botón"
    inlineKeyboard {
        row {
            button {
                text = "Comprobar"
                callbackData = "check_button"
            }
        }
    }
}
```

## Responde a la pulsación

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
    text = "¡El botón funciona!"
    showAlert = true
}
```

Importaciones: `io.chengine.handler.TelegramHandleCallback`, `org.telegram.telegrambots.meta.api.objects.Update` y `org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery`. Responder al callback detiene el indicador de espera del cliente de Telegram.

Si `callbackData` coincide con una ruta de comando registrada, se invoca primero el controlador del comando. El controlador general `@TelegramHandleCallback` procesa los demás eventos callback.

## Enlaces y varias filas

```kotlin
inlineKeyboard {
    row {
        button { text = "Atrás"; callbackData = "prev" }
        button { text = "Siguiente"; callbackData = "next" }
    }
    row { button { text = "Documentación"; url = "https://chengine-project.github.io/chengine/" } }
}
```

Cada botón debe tener exactamente una acción. Los datos de callback pueden ocupar entre 1 y 64 bytes UTF-8. El [ejemplo de catálogo](https://github.com/chengine-project/chengine/tree/develop/examples/catalog) muestra paginación y una ficha de producto en un solo mensaje.

## Editar el mensaje y el teclado

`editTelegramMessageText` actualiza el texto y, opcionalmente, el teclado; `editTelegramMessageReplyMarkup` cambia solo el teclado y su `removeKeyboard()` lo elimina. Indica el `messageId`; `chatId` puede especificarse o tomarse del evento entrante. Para mensajes inline usa `inlineMessageId` en lugar de esos dos campos. Responde al callback antes de editar.

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = listOf(
    AnswerCallbackQuery(update.callbackQuery.id),
    editTelegramMessageText {
        messageId = update.callbackQuery.message.messageId
        text = "Listo"
        inlineKeyboard { row { button { text = "Sitio web"; url = "https://chengine-project.github.io/chengine/" } } }
    }
)
```

## Teclado de respuesta

```kotlin
sendTelegramMessage {
    text = "Elige una respuesta"
    replyKeyboard { row { button { text = "Sí" }; button { text = "No" } } }
}
```

Para quitar el teclado de respuesta, envía un mensaje con `replyKeyboard { removeKeyboard() }`. Un mensaje admite un teclado de respuesta o uno inline, no ambos.

[Ejemplo completo en el repositorio](https://github.com/chengine-project/chengine/tree/develop/examples/inline-keyboard)
