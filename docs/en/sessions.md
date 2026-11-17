---
layout: default
lang: en
title: Sessions and conversations
section: FEATURES
permalink: /en/sessions/
lead: Keep a user’s step and data between messages.
---

## Start a conversation

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.session.Conversations

@HandleCommand("/start")
fun start(request: BotRequestContext) = sendTelegramMessage {
    Conversations.start(request, "name")
    text = "What is your name?"
}
```

The incoming event must contain a user and a chat to create a session. Check `request.sessionKey()` before calling `start`.

## Handle the answer

```kotlin
@TelegramHandleText
fun answer(request: BotRequestContext) = sendTelegramMessage {
    val session = Conversations.current()
    val answer = request.textContent()?.trim().orEmpty()
    text = when {
        session == null -> "Start with /start"
        session.step == "name" -> {
            session["name"] = answer
            session.step = "city"
            "Which city are you in?"
        }
        session.step == "city" -> {
            val name = session["name"]
            Conversations.end()
            "Nice to meet you, $name! Your city: $answer."
        }
        else -> "Start with /start"
    }
}
```

Call `Conversations.end()` in a `/cancel` handler to cancel. This example uses sessions directly; the [complete example]({{ '/en/examples/#conversation' | relative_url }}) uses a graph and checks for empty answers and missing session keys.

## Storage and limits

Sessions are stored in memory by default and lost after a restart. The default inactivity timeout is 10 minutes; change it with the `timeout` and `unit` arguments to `Conversations.start`.

The key includes platform, bot account, user, and chat. Telegram uses the configured bot username, so changing it changes the keys. Requests for one session are serialized within one JVM; simultaneous messages have no guaranteed arrival order.

## PostgreSQL

The `session-postgres` module persists `ConversationSession` across restarts. Add `io.chengine:session-postgres:1.0.1-BETA`, configure a PostgreSQL `DataSource`, and register a bean:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Imports: `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`, `javax.sql.DataSource`, and `org.springframework.context.annotation.Bean`. In production, the table can also be created separately when configuring the database schema. Call `deleteExpired()` to remove expired rows. The store supports `ConversationSession`; implement `SessionStore` for a different backend.

PostgreSQL persists state but does not coordinate multiple processes; separate coordination is required. Saving state and sending a message are not one transaction.

## Conversation graph

The experimental `ConversationGraph` adds named nodes, conditional transitions, and waiting for the next answer on top of the same session. [Learn about graphs →]({{ '/en/graphs/' | relative_url }})

[Complete example]({{ '/en/examples/#conversation' | relative_url }})
