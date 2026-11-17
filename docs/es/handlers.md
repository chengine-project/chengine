---
layout: default
lang: es
title: Comandos y controladores
section: ASPECTOS BÁSICOS
permalink: /es/handlers/
lead: Declara métodos que se ejecutan según un comando, texto o tipo de evento de Telegram.
---

## Comandos

Anota el componente Spring con `@Handler` y el método con `@HandleCommand`. Devuelve un método de Telegram, por ejemplo el resultado de `sendTelegramMessage`.

```kotlin
@Handler
@Component
class MenuHandler {
    @HandleCommand("/help")
    fun help() = sendTelegramMessage {
        text = "¿En qué puedo ayudarte?"
    }
}
```

En grupos se admite `/help@bot_username`. Los argumentos después de un espacio no forman parte de la ruta del comando; el texto completo permanece en `Update`.

```kotlin
@HandleCommand("/start")
fun start(update: Update) = sendTelegramMessage {
    text = "Recibido: ${update.message.text}"
}
```

Añade la importación `org.telegram.telegrambots.meta.api.objects.Update`. Los comandos desconocidos o incorrectos se ignoran; los errores de registro y las rutas duplicadas se detectan al iniciar.

## Coincidencia exacta de texto

```kotlin
@HandleText("Ayuda")
fun helpText() = sendTelegramMessage { text = "Ayuda" }
```

La anotación está en `io.chengine.text.HandleText`. Para otros textos puedes usar `@TelegramHandleText` de `io.chengine.handler`.

## Varias respuestas

Un controlador puede devolver una lista de métodos de Telegram:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "Primer mensaje" },
    sendTelegramMessage { text = "Segundo mensaje" }
)
```

Las respuestas se envían secuencialmente en el orden de la lista. Si un envío falla, no se ejecutan los métodos siguientes; los mensajes ya enviados no se revierten. Una lista vacía no envía nada.

## Prioridad de controladores

Primero se busca el comando, luego una coincidencia exacta de texto y, por último, el controlador general del tipo de evento. Para rutas de comandos Chengine también admite el formato `/item/id#42` con el parámetro `@CommandParameter("id")`.

[Ejemplo de diálogo →]({{ '/es/sessions/' | relative_url }})
