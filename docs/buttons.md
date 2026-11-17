---
layout: default
title: Кнопки и callback
section: ВОЗМОЖНОСТИ
permalink: /buttons/
lead: Добавляйте inline-клавиатуру к сообщению и обрабатывайте нажатия.
---

## Отправьте кнопку

```kotlin
@HandleCommand("/start")
fun start() = sendTelegramMessage {
    text = "Нажмите кнопку"
    inlineKeyboard {
        row {
            button {
                text = "Проверить"
                callbackData = "check_button"
            }
        }
    }
}
```

## Ответьте на нажатие

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
    text = "Кнопка работает!"
    showAlert = true
}
```

Импорты: `io.chengine.handler.TelegramHandleCallback`, `org.telegram.telegrambots.meta.api.objects.Update` и `org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery`. Ответ на callback снимает индикатор ожидания в клиенте Telegram.

Если `callbackData` совпадает с зарегистрированным command-путём, сначала вызывается обработчик команды. Общий `@TelegramHandleCallback` обрабатывает оставшиеся callback-события.

[Полный пример в репозитории](https://github.com/chengine-project/chengine/tree/develop/examples/inline-keyboard)
