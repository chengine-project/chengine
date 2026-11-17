---
layout: default
lang: en
title: Commands and handlers
section: GETTING STARTED
permalink: /en/handlers/
lead: Declare methods invoked for a command, text, or Telegram event type.
---

## Commands

Mark a Spring component with `@Handler` and a method with `@HandleCommand`. Return a Telegram method, such as the result of `sendTelegramMessage`.

```kotlin
@Handler
@Component
class MenuHandler {
    @HandleCommand("/help")
    fun help() = sendTelegramMessage {
        text = "How can I help?"
    }
}
```

Group chats support commands such as `/help@bot_username`. Arguments after whitespace are not part of the command path; the full text remains available in `Update`.

```kotlin
@HandleCommand("/start")
fun start(update: Update) = sendTelegramMessage {
    text = "Received: ${update.message.text}"
}
```

Import `org.telegram.telegrambots.meta.api.objects.Update`. Unknown and malformed commands are ignored; registration errors and duplicate paths are detected at startup.

## Exact text matches

```kotlin
@HandleText("Help")
fun helpText() = sendTelegramMessage { text = "Help information" }
```

The annotation is `io.chengine.text.HandleText`. Use `@TelegramHandleText` from `io.chengine.handler` for other text.

## Multiple responses

A handler can return a list of Telegram methods:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "First message" },
    sendTelegramMessage { text = "Second message" }
)
```

Responses are sent sequentially in list order. If sending fails, subsequent methods are not executed; earlier messages are not rolled back. An empty list sends nothing.

## Handler priority

Chengine looks for a command first, then an exact text match, then a general event-type handler. Chengine also supports command paths like `/item/id#42` with `@CommandParameter("id")`.

[Conversation example →]({{ '/en/sessions/' | relative_url }})
