---
layout: default
lang: es
title: Encuestas y cuestionarios
section: FUNCIONALIDADES
permalink: /es/polls/
lead: Crea encuestas y gestiona tanto las respuestas individuales como los cambios del estado agregado.
---

## Encuesta normal

```kotlin
import io.chengine.dsl.sendTelegramPoll

@HandleCommand("/poll")
fun poll() = sendTelegramPoll {
    question = "¿Gatos o perros?"
    options = listOf("Gatos", "Perros")
    isAnonymous = false
}
```

Telegram crea encuestas anónimas de forma predeterminada. Para recibir respuestas individuales, indica `isAnonymous = false`.

## Cuestionario

```kotlin
import io.chengine.dsl.TelegramPollType

@HandleCommand("/quiz")
fun quiz() = sendTelegramPoll {
    question = "¿Cuánto es 2 + 2?"
    options = listOf("3", "4", "5")
    type = TelegramPollType.QUIZ
    correctOptionIds = listOf(1)
    explanation = "2 + 2 = 4"
    isAnonymous = false
}
```

Los índices de las respuestas correctas empiezan en cero y se indican en orden ascendente. El DSL valida la pregunta, el número y la longitud de las opciones, los índices y algunos parámetros de tiempo.

## Eventos de votación

Usa `@TelegramHandlePollAnswer` para una respuesta individual y `@TelegramHandlePoll` para el estado de la encuesta. Ambos controladores reciben el `Update` original.

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.telegramPollOrigin

@TelegramHandlePollAnswer
fun answer(update: Update, request: BotRequestContext) =
    if (request.telegramPollOrigin() != null) sendTelegramMessage {
        text = if (update.pollAnswer.optionIds.isEmpty()) "Voto retirado" else "¡Gracias por tu respuesta!"
    } else Unit
```

`telegramPollOrigin()` devuelve el ID de la encuesta, el chat original y el ID del mensaje cuando se conoce la asociación. Si el controlador devuelve un mensaje sin `chatId` explícito, Chengine lo envía al chat original. El usuario puede retirar su voto: entonces `optionIds` está vacío. Puede aparecer `voterChat` en lugar de `user`.

Por defecto, las asociaciones se guardan en un almacén en memoria de tamaño limitado y se pierden al reiniciar. Para almacenamiento persistente, implementa `TelegramPollOriginStore` y asígnalo a `ChengineTelegramLongPoolingBot.pollOriginStore` antes de procesar eventos. Puede no haber asociación para encuestas enviadas fuera de Chengine, cuando la entrada ha caducado o si un evento llega mientras se guarda. Las encuestas no crean sesiones; el chat original no es necesariamente el chat del votante.

> Las respuestas individuales solo llegan para encuestas no anónimas enviadas por el bot. Las encuestas anónimas producen eventos de estado agregado.

[Ejemplo completo]({{ '/es/examples/#polls' | relative_url }})
