---
layout: default
lang: de
title: Vollständige Beispiele
section: BEISPIELE
permalink: /de/examples/
lead: Fertige Handler, die du direkt in der Dokumentation ansehen und kopieren kannst.
---

Alle Beispiele sind für eine Spring-Boot-Anwendung mit dem Telegram-Starter gedacht. Erstelle ein Projekt anhand des [Schnellstarts]({{ '/de/start/' | relative_url }}) und füge anschließend **einen** Handler in dein Anwendungspaket ein. Einige Beispiele verwenden gleiche Befehle oder allgemeine Ereignishandler. Aktiviere deshalb nicht alle gleichzeitig, ohne sie anzupassen.

- [Katalog](#catalog) — Buttons, Übergänge und Nachrichtenbearbeitung.
- [Dialog](#conversation) — Name und Stadt mit einem Dialoggraphen.
- [Buttons](#buttons) — Callback und Link.
- [Bearbeitung](#editing) — Text und Tastatur ändern.
- [Medien](#media) — Dateien senden.
- [Umfragen](#polls) — abstimmen und im ursprünglichen Chat antworten.
- [Mehrere Nachrichten](#multiple) — Antworten in Reihenfolge.
- [Tastatur und Video](#more-dsl) — Reply-Tastatur und Videonachricht.
- [PostgreSQL](#postgres) — persistente Sitzungen.
- [Agentenwerkzeug](#agent) — Nachrichten an den aktuellen Chat.

## Katalog {#catalog}

Der Befehl `/catalog` öffnet einen Katalog mit Seitenwechsel und Produktkarten. Jeder Übergang aktualisiert dieselbe Nachricht.

<details markdown="1">
<summary>CatalogHandler.kt anzeigen</summary>
{% highlight kotlin %}{% include examples/de/CatalogHandler.kt %}{% endhighlight %}
</details>

## Dialog {#conversation}

Der Befehl `/start` beginnt einen Dialog zu Name und Stadt. Mit `überspringen` kannst du die Stadt auslassen; `/cancel` beendet den Ablauf. Die Daten werden standardmäßig im Arbeitsspeicher gehalten.

<details markdown="1">
<summary>ConversationHandler.kt anzeigen</summary>
{% highlight kotlin %}{% include examples/de/ConversationHandler.kt %}{% endhighlight %}
</details>

## Inline-Buttons {#buttons}

Der Befehl `/start` sendet einen Callback-Button und einen Link zur Dokumentation.

<details markdown="1">
<summary>InlineKeyboardHandler.kt anzeigen</summary>
{% highlight kotlin %}{% include examples/de/InlineKeyboardHandler.kt %}{% endhighlight %}
</details>

## Nachricht bearbeiten {#editing}

Sende `/edit` und drücke die Buttons, um zuerst den Text und danach die Tastatur derselben Nachricht zu ändern.

<details markdown="1">
<summary>EditMessageHandler.kt anzeigen</summary>
{% highlight kotlin %}{% include examples/de/EditMessageHandler.kt %}{% endhighlight %}
</details>

## Fotos, Dokumente, Video und Audio {#media}

Lege eigene Dateien `photo.jpg`, `document.pdf`, `video.mp4` und `audio.mp3` im Arbeitsverzeichnis der Anwendung ab und verwende `/photo`, `/document`, `/video` und `/audio`.

<details markdown="1">
<summary>MediaHandler.kt anzeigen</summary>
{% highlight kotlin %}{% include examples/de/MediaHandler.kt %}{% endhighlight %}
</details>

## Umfrage und Quiz {#polls}

Die Befehle `/poll` und `/quiz` erstellen nicht anonyme Umfragen. Nach der Abstimmung kann der Bot im ursprünglichen Chat antworten, solange die Umfragezuordnung verfügbar ist.

<details markdown="1">
<summary>PollHandler.kt anzeigen</summary>
{% highlight kotlin %}{% include examples/de/PollHandler.kt %}{% endhighlight %}
</details>

Mehr zur Speicherkonfiguration steht unter [Sitzungen und PostgreSQL]({{ '/de/sessions/' | relative_url }}) und [Umfragen]({{ '/de/polls/' | relative_url }}). [Alle Beispielquellen](https://github.com/chengine-project/chengine/tree/develop/examples) sind auch im Repository verfügbar.

## Mehrere Nachrichten {#multiple}

Ein Handler kann eine Liste von Antworten zurückgeben; sie werden nacheinander gesendet:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "Erste Nachricht" },
    sendTelegramMessage { text = "Zweite Nachricht" }
)
```

## Tastatur und Videonachricht {#more-dsl}

```kotlin
sendTelegramVideoNote { videoNote { file = File("note.mp4") } }
sendTelegramMessage {
    text = "Wähle eine Antwort"
    replyKeyboard { row { button { text = "Ja" }; button { text = "Nein" } } }
}
sendTelegramMessage { text = "Fertig"; replyKeyboard { removeKeyboard() } }
```

Für eine Videonachricht wird eine geeignete quadratische Videodatei benötigt. Eine Antworttastatur und eine Inline-Tastatur können nicht an dieselbe Nachricht angehängt werden.

## Sitzungen in PostgreSQL speichern {#postgres}

Binde das Modul `session-postgres` ein, konfiguriere eine `DataSource` und registriere eine Bean:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Importe: `org.springframework.context.annotation.Bean`, `javax.sql.DataSource`, `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`. Weitere Informationen stehen unter [Sitzungen]({{ '/de/sessions/' | relative_url }}).

## Werkzeug für Agenten {#agent}

Das experimentelle Werkzeug sendet Text an den Chat der aktuellen Anfrage und gibt die Nachrichten-ID zurück. Die Anwendung stellt `request` und `client` bereit:

```kotlin
fun sendToCurrentChat(request: BotRequestContext, client: TelegramClient): Int =
    TelegramSendMessageTool(request, client).execute("Hallo!")
```

Importe: `io.chengine.connector.BotRequestContext`, `org.telegram.telegrambots.meta.generics.TelegramClient`, `io.chengine.agent.TelegramSendMessageTool`. Dieses Beispiel enthält weder ein reales Modell noch eine automatische Schleife für Werkzeugaufrufe. Weitere Informationen stehen unter [Graphen]({{ '/de/graphs/' | relative_url }}).
