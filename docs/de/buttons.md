---
layout: default
lang: de
title: Buttons und Callbacks
section: FUNKTIONEN
permalink: /de/buttons/
lead: Inline-Tastaturen an Nachrichten anhängen und Klicks verarbeiten.
---

## Button senden

```kotlin
@HandleCommand("/start")
fun start() = sendTelegramMessage {
    text = "Drücken Sie den Button"
    inlineKeyboard {
        row {
            button {
                text = "Prüfen"
                callbackData = "check_button"
            }
        }
    }
}
```

## Callback beantworten

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
    text = "Der Button funktioniert!"
    showAlert = true
}
```

Importe: `io.chengine.handler.TelegramHandleCallback`, `org.telegram.telegrambots.meta.api.objects.Update` und `org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery`. Die Callback-Antwort beendet die Ladeanzeige im Telegram-Client.

Stimmt `callbackData` mit einem registrierten Befehlspfad überein, wird zuerst der Befehlshandler aufgerufen. Der allgemeine `@TelegramHandleCallback` verarbeitet die übrigen Callbacks.

## Links und mehrere Reihen

```kotlin
inlineKeyboard {
    row {
        button { text = "Zurück"; callbackData = "prev" }
        button { text = "Weiter"; callbackData = "next" }
    }
    row { button { text = "Dokumentation"; url = "https://chengine-project.github.io/chengine/" } }
}
```

Jeder Button benötigt genau eine Aktion. Callback-Daten dürfen 1–64 UTF-8-Bytes umfassen. Das [Katalogbeispiel](https://github.com/chengine-project/chengine/tree/develop/examples/catalog) zeigt Seitenwechsel und Produktkarte in einer Nachricht.

## Nachricht und Tastatur bearbeiten

`editTelegramMessageText` aktualisiert den Text und optional die Tastatur; `editTelegramMessageReplyMarkup` ändert nur die Tastatur, und dessen `removeKeyboard()` entfernt sie. Gib die `messageId` an; `chatId` kann explizit sein oder aus dem Ereignis stammen. Für Inline-Nachrichten verwende stattdessen `inlineMessageId`. Beantworte den Callback vor der Bearbeitung.

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = listOf(
    AnswerCallbackQuery(update.callbackQuery.id),
    editTelegramMessageText {
        messageId = update.callbackQuery.message.messageId
        text = "Fertig"
        inlineKeyboard { row { button { text = "Website"; url = "https://chengine-project.github.io/chengine/" } } }
    }
)
```

## Antworttastatur

```kotlin
sendTelegramMessage {
    text = "Wählen Sie eine Antwort"
    replyKeyboard { row { button { text = "Ja" }; button { text = "Nein" } } }
}
```

Zum Entfernen einer Antworttastatur sende eine Nachricht mit `replyKeyboard { removeKeyboard() }`. Eine Nachricht akzeptiert entweder eine Antwort- oder eine Inline-Tastatur.

[Vollständiges Beispiel im Repository](https://github.com/chengine-project/chengine/tree/develop/examples/inline-keyboard)
