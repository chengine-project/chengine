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

## Ссылки и несколько рядов

```kotlin
inlineKeyboard {
    row {
        button { text = "Назад"; callbackData = "prev" }
        button { text = "Вперёд"; callbackData = "next" }
    }
    row { button { text = "Документация"; url = "https://chengine-project.github.io/chengine/" } }
}
```

У каждой кнопки должно быть ровно одно действие. Для callback допустимы данные длиной 1–64 байта UTF-8. [Пример каталога](https://github.com/chengine-project/chengine/tree/develop/examples/catalog) показывает переключение страниц и карточку товара в одном сообщении.

## Изменение сообщения и клавиатуры

`editTelegramMessageText` обновляет текст и при необходимости клавиатуру; `editTelegramMessageReplyMarkup` меняет только клавиатуру, а его `removeKeyboard()` убирает её. Укажите `messageId` сообщения; `chatId` можно задать явно или взять из входящего события. Для inline-сообщений задайте `inlineMessageId` вместо этих двух полей. Перед редактированием отвечайте на callback.

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = listOf(
    AnswerCallbackQuery(update.callbackQuery.id),
    editTelegramMessageText {
        messageId = update.callbackQuery.message.messageId
        text = "Готово"
        inlineKeyboard { row { button { text = "Сайт"; url = "https://chengine-project.github.io/chengine/" } } }
    }
)
```

## Reply-клавиатура

```kotlin
sendTelegramMessage {
    text = "Выберите ответ"
    replyKeyboard { row { button { text = "Да" }; button { text = "Нет" } } }
}
```

Чтобы убрать reply-клавиатуру, отправьте сообщение с `replyKeyboard { removeKeyboard() }`. Одно сообщение принимает либо reply-, либо inline-клавиатуру.

[Полный пример в репозитории](https://github.com/chengine-project/chengine/tree/develop/examples/inline-keyboard)
