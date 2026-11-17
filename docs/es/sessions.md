---
layout: default
lang: es
title: Sesiones y diálogos
section: FUNCIONALIDADES
permalink: /es/sessions/
lead: Conserva el paso y los datos del usuario entre mensajes.
---

## Inicia un diálogo

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.session.Conversations

@HandleCommand("/start")
fun start(request: BotRequestContext) = sendTelegramMessage {
    Conversations.start(request, "name")
    text = "¿Cómo te llamas?"
}
```

Para crear una sesión, el evento entrante debe incluir el usuario y el chat. Puedes comprobar `request.sessionKey()` antes de llamar a `start`.

## Procesa la respuesta

```kotlin
@TelegramHandleText
fun answer(request: BotRequestContext) = sendTelegramMessage {
    val session = Conversations.current()
    val answer = request.textContent()?.trim().orEmpty()
    text = when {
        session == null -> "Comienza con /start"
        session.step == "name" -> {
            session["name"] = answer
            session.step = "city"
            "¿En qué ciudad estás?"
        }
        session.step == "city" -> {
            val name = session["name"]
            Conversations.end()
            "¡Mucho gusto, $name! Tu ciudad es: $answer."
        }
        else -> "Comienza con /start"
    }
}
```

Para cancelar, llama a `Conversations.end()` en el controlador de `/cancel`. Este ejemplo usa la sesión directamente; el [ejemplo completo]({{ '/es/examples/#conversation' | relative_url }}) utiliza un grafo y comprueba las respuestas vacías y la ausencia de clave.

## Almacenamiento y límites

Por defecto, las sesiones se guardan en memoria y se pierden al reiniciar. El tiempo de inactividad predeterminado es de 10 minutos; puedes cambiarlo con `timeout` y `unit` al llamar a `Conversations.start`.

La clave incluye la plataforma, la cuenta del bot, el usuario y el chat. Para Telegram se usa el nombre configurado del bot; cambiarlo cambia las claves. Las solicitudes de una misma sesión se procesan secuencialmente dentro de una JVM; no se garantiza el orden de los mensajes que llegan simultáneamente.

## PostgreSQL

El módulo `session-postgres` conserva `ConversationSession` entre reinicios. Añade la dependencia `io.github.chengine-project:session-postgres:1.0.0`, configura un `DataSource` de PostgreSQL y registra un bean:

Consulta las versiones disponibles de este módulo en [Maven Central](https://central.sonatype.com/artifact/io.github.chengine-project/session-postgres).

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Importaciones: `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`, `javax.sql.DataSource` y `org.springframework.context.annotation.Bean`. En una aplicación de producción también puedes crear la tabla por separado al configurar el esquema. `deleteExpired()` elimina las entradas caducadas. El almacén admite `ConversationSession`; la interfaz `SessionStore` permite implementar otro almacén.

PostgreSQL conserva el estado, pero no sincroniza varios procesos: necesitan coordinación adicional. Guardar el estado y enviar el mensaje no forman una sola transacción.

## Grafo del flujo

El `ConversationGraph` experimental añade nodos con nombre, transiciones condicionales y espera de la siguiente respuesta sobre la misma sesión. [Cómo funcionan los grafos →]({{ '/es/graphs/' | relative_url }})

[Ejemplo completo]({{ '/es/examples/#conversation' | relative_url }})
