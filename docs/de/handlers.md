---
layout: default
lang: de
title: Befehle und Handler
section: GRUNDLAGEN
permalink: /de/handlers/
lead: Methoden deklarieren, die auf Befehle, Text oder Telegram-Ereignisse reagieren.
---

## Befehle

Markiere eine Spring-Komponente mit `@Handler` und eine Methode mit `@HandleCommand`. Gib eine Telegram-Methode zurück, beispielsweise das Ergebnis von `sendTelegramMessage`.

```kotlin
@Handler
@Component
class MenuHandler {
    @HandleCommand("/help")
    fun help() = sendTelegramMessage {
        text = "Wie kann ich helfen?"
    }
}
```

In Gruppen werden Befehle wie `/help@bot_username` unterstützt. Argumente nach einem Leerzeichen gehören nicht zum Befehlspfad; der vollständige Text bleibt in `Update` verfügbar.

```kotlin
@HandleCommand("/start")
fun start(update: Update) = sendTelegramMessage {
    text = "Empfangen: ${update.message.text}"
}
```

Importiere `org.telegram.telegrambots.meta.api.objects.Update`. Unbekannte und fehlerhafte Befehle werden ignoriert; Registrierungsfehler und doppelte Pfade werden beim Start erkannt.

## Exakter Text

```kotlin
@HandleText("Hilfe")
fun helpText() = sendTelegramMessage { text = "Hilfe" }
```

Die Annotation ist `io.chengine.text.HandleText`. Für übrigen Text kann `@TelegramHandleText` aus `io.chengine.handler` verwendet werden.

## Mehrere Antworten

Ein Handler kann eine Liste von Telegram-Methoden zurückgeben:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "Erste Nachricht" },
    sendTelegramMessage { text = "Zweite Nachricht" }
)
```

Antworten werden nacheinander in Listenreihenfolge gesendet. Schlägt ein Versand fehl, werden die folgenden Methoden nicht ausgeführt; bereits gesendete Nachrichten werden nicht zurückgenommen. Eine leere Liste sendet nichts.

## Priorität der Handler

Zuerst wird ein Befehl gesucht, dann eine exakte Textübereinstimmung und zuletzt ein allgemeiner Handler für den Ereignistyp. Für Befehlspfade unterstützt Chengine auch `/item/id#42` mit `@CommandParameter("id")`.

[Dialogbeispiel →]({{ '/de/sessions/' | relative_url }})

## Textantworten

Ein Handler kann einen `String` zurückgeben; Chengine sendet ihn an den Chat des Ereignisses. Verwende für formatierten Text `sendTelegramMessage` mit `parseMode = ParseMode.MARKDOWN_V2`. Maskiere Sonderzeichen nach den Telegram-Regeln.

```kotlin
@HandleCommand("/hello")
fun hello(): String = "Hello!"
```
