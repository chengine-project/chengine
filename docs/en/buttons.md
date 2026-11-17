---
layout: default
lang: en
title: Buttons and callbacks
section: FEATURES
permalink: /en/buttons/
lead: Add an inline keyboard to a message and handle button presses.
---

## Send a button

```kotlin
@HandleCommand("/start")
fun start() = sendTelegramMessage {
    text = "Press the button"
    inlineKeyboard {
        row {
            button {
                text = "Check"
                callbackData = "check_button"
            }
        }
    }
}
```

## Answer a callback

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
    text = "The button works!"
    showAlert = true
}
```

Imports: `io.chengine.handler.TelegramHandleCallback`, `org.telegram.telegrambots.meta.api.objects.Update`, and `org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery`. Answering a callback dismisses the Telegram client's progress indicator.

If `callbackData` matches a registered command path, that command runs first. The general `@TelegramHandleCallback` handles remaining callbacks.

## Links and multiple rows

```kotlin
inlineKeyboard {
    row {
        button { text = "Back"; callbackData = "prev" }
        button { text = "Next"; callbackData = "next" }
    }
    row { button { text = "Documentation"; url = "https://chengine-project.github.io/chengine/" } }
}
```

Each button must have exactly one action. Callback data must be 1–64 UTF-8 bytes. The [catalog example](https://github.com/chengine-project/chengine/tree/develop/examples/catalog) shows page navigation and a product card within one message.

## Editing a message and keyboard

`editTelegramMessageText` updates text and optionally the keyboard; `editTelegramMessageReplyMarkup` changes only the keyboard, and its `removeKeyboard()` removes it. Supply the message `messageId`; `chatId` may be explicit or inferred from the incoming event. For inline messages, use `inlineMessageId` instead of both. Answer the callback before editing.

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = listOf(
    AnswerCallbackQuery(update.callbackQuery.id),
    editTelegramMessageText {
        messageId = update.callbackQuery.message.messageId
        text = "Done"
        inlineKeyboard { row { button { text = "Website"; url = "https://chengine-project.github.io/chengine/" } } }
    }
)
```

## Reply keyboard

```kotlin
sendTelegramMessage {
    text = "Choose an answer"
    replyKeyboard { row { button { text = "Yes" }; button { text = "No" } } }
}
```

To remove a reply keyboard, send a message with `replyKeyboard { removeKeyboard() }`. A message accepts either a reply or an inline keyboard.

[Complete example in the repository](https://github.com/chengine-project/chengine/tree/develop/examples/inline-keyboard)
