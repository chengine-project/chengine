---
layout: default
lang: en
title: Complete examples
section: EXAMPLES
permalink: /en/examples/
lead: Ready-to-use handlers you can explore and copy directly from the documentation.
---

All examples are designed for a Spring Boot application with the Telegram starter. Create a project using the [quick start]({{ '/en/start/' | relative_url }}), then add **one** handler to your application package. Some examples share commands or general event handlers, so do not enable them all at once without adapting them.

- [Catalog](#catalog) — buttons, transitions, and message editing.
- [Conversation](#conversation) — name and city in a conversation graph.
- [Buttons](#buttons) — callback and link.
- [Editing](#editing) — updating text and keyboard.
- [Media](#media) — sending files.
- [Polls](#polls) — voting and replying to the original chat.
- [Multiple messages](#multiple) — ordered responses.
- [Keyboard and video](#more-dsl) — reply buttons and video notes.
- [PostgreSQL](#postgres) — persistent sessions.
- [Agent tool](#agent) — sending to the current chat.

## Catalog {#catalog}

The `/catalog` command opens a catalog with navigation and product cards. Each transition updates the same message.

<details markdown="1">
<summary>Show CatalogHandler.kt</summary>
{% highlight kotlin %}{% include examples/en/CatalogHandler.kt %}{% endhighlight %}
</details>

## Conversation {#conversation}

The `/start` command begins a conversation about your name and city. To skip the city, type `skip`; `/cancel` ends the flow. Data is stored in memory by default.

<details markdown="1">
<summary>Show ConversationHandler.kt</summary>
{% highlight kotlin %}{% include examples/en/ConversationHandler.kt %}{% endhighlight %}
</details>

## Inline buttons {#buttons}

The `/start` command sends a callback button and a link to the documentation.

<details markdown="1">
<summary>Show InlineKeyboardHandler.kt</summary>
{% highlight kotlin %}{% include examples/en/InlineKeyboardHandler.kt %}{% endhighlight %}
</details>

## Editing a message {#editing}

Send `/edit` and press the buttons to change the text and then the keyboard of the same message.

<details markdown="1">
<summary>Show EditMessageHandler.kt</summary>
{% highlight kotlin %}{% include examples/en/EditMessageHandler.kt %}{% endhighlight %}
</details>

## Photos, documents, video and audio {#media}

Place your own `photo.jpg`, `document.pdf`, `video.mp4` and `audio.mp3` in the application's working directory, then use `/photo`, `/document`, `/video` and `/audio`.

<details markdown="1">
<summary>Show MediaHandler.kt</summary>
{% highlight kotlin %}{% include examples/en/MediaHandler.kt %}{% endhighlight %}
</details>

## Poll and quiz {#polls}

The `/poll` and `/quiz` commands create non-anonymous polls. After a vote, the bot can reply in the original chat while the poll mapping is available.

<details markdown="1">
<summary>Show PollHandler.kt</summary>
{% highlight kotlin %}{% include examples/en/PollHandler.kt %}{% endhighlight %}
</details>

For storage configuration, see [sessions and PostgreSQL]({{ '/en/sessions/' | relative_url }}) and [polls]({{ '/en/polls/' | relative_url }}). [All example sources](https://github.com/chengine-project/chengine/tree/develop/examples) are also available in the repository.

## Multiple messages {#multiple}

A handler can return a list of responses; they are sent sequentially:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "First message" },
    sendTelegramMessage { text = "Second message" }
)
```

## Keyboard and video note {#more-dsl}

```kotlin
sendTelegramVideoNote { videoNote { file = File("note.mp4") } }
sendTelegramMessage {
    text = "Choose an answer"
    replyKeyboard { row { button { text = "Yes" }; button { text = "No" } } }
}
sendTelegramMessage { text = "Done"; replyKeyboard { removeKeyboard() } }
```

A video note needs a suitable square video file. A reply keyboard and an inline keyboard cannot be attached to the same message.

## PostgreSQL session storage {#postgres}

Add the `session-postgres` module, configure a `DataSource`, and register a bean:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Imports: `org.springframework.context.annotation.Bean`, `javax.sql.DataSource`, `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`. See [sessions]({{ '/en/sessions/' | relative_url }}) for details.

## Agent tool {#agent}

The experimental tool sends text to the chat of the current request and returns the message ID. The application provides `request` and `client`:

```kotlin
fun sendToCurrentChat(request: BotRequestContext, client: TelegramClient): Int =
    TelegramSendMessageTool(request, client).execute("Hello!")
```

Imports: `io.chengine.connector.BotRequestContext`, `org.telegram.telegrambots.meta.generics.TelegramClient`, `io.chengine.agent.TelegramSendMessageTool`. This example does not include a real model or an automatic tool-calling loop. See [graphs]({{ '/en/graphs/' | relative_url }}) for details.
