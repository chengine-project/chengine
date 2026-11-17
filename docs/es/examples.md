---
layout: default
lang: es
title: Ejemplos completos
section: EJEMPLOS
permalink: /es/examples/
lead: Controladores listos para estudiar y copiar directamente desde la documentación.
---

Todos los ejemplos están diseñados para una aplicación Spring Boot con el starter de Telegram. Crea el proyecto siguiendo la [guía de inicio rápido]({{ '/es/start/' | relative_url }}) y añade **un** controlador al paquete de la aplicación. Algunos ejemplos usan los mismos comandos o controladores generales de eventos, así que no los actives todos a la vez sin adaptarlos.

- [Catálogo](#catalog): botones, transiciones y edición de mensajes.
- [Diálogo](#conversation): nombre y ciudad mediante un grafo de flujo.
- [Botones](#buttons): callback y enlace.
- [Edición](#editing): modificación del texto y del teclado.
- [Multimedia](#media): envío de archivos.
- [Encuestas](#polls): votación y respuesta en el chat original.
- [Varios mensajes](#multiple): respuestas enviadas en orden.
- [Teclado y vídeo](#more-dsl): teclado de respuesta y mensaje de vídeo.
- [PostgreSQL](#postgres): almacenamiento de sesiones.
- [Herramienta para agentes](#agent): envío de texto al chat actual.

## Catálogo {#catalog}

El comando `/catalog` abre un catálogo con navegación y fichas de productos. Cada transición actualiza el mismo mensaje.

<details markdown="1">
<summary>Mostrar CatalogHandler.kt</summary>
{% highlight kotlin %}{% include examples/es/CatalogHandler.kt %}{% endhighlight %}
</details>

## Diálogo {#conversation}

El comando `/start` inicia un diálogo sobre nombre y ciudad; se puede omitir la ciudad escribiendo «omitir», y `/cancel` termina el flujo. Por defecto, los datos se guardan en memoria.

<details markdown="1">
<summary>Mostrar ConversationHandler.kt</summary>
{% highlight kotlin %}{% include examples/es/ConversationHandler.kt %}{% endhighlight %}
</details>

## Botones inline {#buttons}

El comando `/start` envía un botón con callback y un enlace a la documentación.

<details markdown="1">
<summary>Mostrar InlineKeyboardHandler.kt</summary>
{% highlight kotlin %}{% include examples/es/InlineKeyboardHandler.kt %}{% endhighlight %}
</details>

## Edición de mensajes {#editing}

Envía `/edit` y pulsa los botones para cambiar primero el texto y luego el teclado del mismo mensaje.

<details markdown="1">
<summary>Mostrar EditMessageHandler.kt</summary>
{% highlight kotlin %}{% include examples/es/EditMessageHandler.kt %}{% endhighlight %}
</details>

## Fotos, documentos, vídeo y audio {#media}

Coloca tus propios archivos `photo.jpg`, `document.pdf`, `video.mp4` y `audio.mp3` en el directorio de trabajo de la aplicación y usa los comandos `/photo`, `/document`, `/video` y `/audio`.

<details markdown="1">
<summary>Mostrar MediaHandler.kt</summary>
{% highlight kotlin %}{% include examples/es/MediaHandler.kt %}{% endhighlight %}
</details>

## Encuesta y cuestionario {#polls}

Los comandos `/poll` y `/quiz` crean encuestas no anónimas. Tras votar, el bot puede responder en el chat original mientras la asociación con la encuesta esté disponible.

<details markdown="1">
<summary>Mostrar PollHandler.kt</summary>
{% highlight kotlin %}{% include examples/es/PollHandler.kt %}{% endhighlight %}
</details>

Más información sobre el almacenamiento en [sesiones y PostgreSQL]({{ '/es/sessions/' | relative_url }}) y [encuestas]({{ '/es/polls/' | relative_url }}). [Todos los ejemplos originales](https://github.com/chengine-project/chengine/tree/develop/examples) también están disponibles en el repositorio.

## Varios mensajes {#multiple}

Un controlador puede devolver una lista de respuestas, que se enviarán en orden:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "Primer mensaje" },
    sendTelegramMessage { text = "Segundo mensaje" }
)
```

## Teclado y mensaje de vídeo {#more-dsl}

```kotlin
sendTelegramVideoNote { videoNote { file = File("note.mp4") } }
sendTelegramMessage {
    text = "Elige una respuesta"
    replyKeyboard { row { button { text = "Sí" }; button { text = "No" } } }
}
sendTelegramMessage { text = "Listo"; replyKeyboard { removeKeyboard() } }
```

Para un mensaje de vídeo se necesita un archivo de vídeo cuadrado adecuado. No se pueden adjuntar un teclado de respuesta y un teclado inline al mismo mensaje.

## Almacenamiento de sesiones en PostgreSQL {#postgres}

Añade el módulo `session-postgres`, configura un `DataSource` y registra un bean:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Importaciones: `org.springframework.context.annotation.Bean`, `javax.sql.DataSource`, `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`. Más información en la [sección de sesiones]({{ '/es/sessions/' | relative_url }}).

## Herramienta para agentes {#agent}

Esta herramienta experimental envía texto al chat de la solicitud actual y devuelve el ID del mensaje. La aplicación proporciona `request` y `client`:

```kotlin
fun sendToCurrentChat(request: BotRequestContext, client: TelegramClient): Int =
    TelegramSendMessageTool(request, client).execute("¡Hola!")
```

Importaciones: `io.chengine.connector.BotRequestContext`, `org.telegram.telegrambots.meta.generics.TelegramClient`, `io.chengine.agent.TelegramSendMessageTool`. Este ejemplo no incluye un modelo real ni un ciclo automático de llamadas a herramientas. Más información en la [sección de grafos]({{ '/es/graphs/' | relative_url }}).
