---
layout: default
lang: de
title: Umfragen und Quizze
section: FUNKTIONEN
permalink: /de/polls/
lead: Umfragen erstellen und sowohl Antworten als auch Änderungen des Gesamtstands verarbeiten.
---

## Reguläre Umfrage

```kotlin
import io.chengine.dsl.sendTelegramPoll

@HandleCommand("/poll")
fun poll() = sendTelegramPoll {
    question = "Katzen oder Hunde?"
    options = listOf("Katzen", "Hunde")
    isAnonymous = false
}
```

Telegram-Umfragen sind standardmäßig anonym. Setze `isAnonymous = false`, um einzelne Antworten zu erhalten.

## Quiz

```kotlin
import io.chengine.dsl.TelegramPollType

@HandleCommand("/quiz")
fun quiz() = sendTelegramPoll {
    question = "Wie viel ist 2 + 2?"
    options = listOf("3", "4", "5")
    type = TelegramPollType.QUIZ
    correctOptionIds = listOf(1)
    explanation = "2 + 2 = 4"
    isAnonymous = false
}
```

Indizes richtiger Antworten beginnen bei null und werden aufsteigend angegeben. Das DSL prüft die Frage, Anzahl und Länge der Optionen, Indizes sowie einige Zeitparameter.

## Abstimmungsereignisse

Verwende `@TelegramHandlePollAnswer` für einzelne Antworten und `@TelegramHandlePoll` für den Umfragestatus. Beide Handler erhalten das ursprüngliche `Update`.

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.telegramPollOrigin

@TelegramHandlePollAnswer
fun answer(update: Update, request: BotRequestContext) =
    if (request.telegramPollOrigin() != null) sendTelegramMessage {
        text = if (update.pollAnswer.optionIds.isEmpty()) "Stimme zurückgezogen" else "Danke für Ihre Antwort!"
    } else Unit
```

`telegramPollOrigin()` liefert, falls bekannt, die Umfrage-ID, den ursprünglichen Chat und die Nachrichten-ID. Gibt der Handler eine Nachricht ohne explizite `chatId` zurück, sendet Chengine sie an den ursprünglichen Chat. Stimmen können zurückgezogen werden; dann ist `optionIds` leer. Statt `user` kann `voterChat` vorhanden sein.

Standardmäßig werden Zuordnungen in einem begrenzten Speicher gehalten und gehen bei einem Neustart verloren. Für dauerhafte Speicherung implementiere `TelegramPollOriginStore` und setze `ChengineTelegramLongPoolingBot.pollOriginStore` vor der Verarbeitung. Bei außerhalb von Chengine gesendeten Umfragen, abgelaufenen Einträgen oder einem Wettlauf beim Senden kann die Zuordnung fehlen. Umfragen erzeugen keine Sitzung; der ursprüngliche Umfrage-Chat ist nicht unbedingt der Chat des Abstimmenden.

> Einzelne Antworten werden nur für nicht anonyme, vom Bot gesendete Umfragen zugestellt. Anonyme Umfragen liefern Ereignisse zum Gesamtstand.

[Vollständiges Beispiel]({{ '/de/examples/#polls' | relative_url }})
