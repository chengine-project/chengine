---
layout: default
title: Опросы и викторины
section: ВОЗМОЖНОСТИ
permalink: /polls/
lead: Создавайте опросы и обрабатывайте как ответы пользователей, так и изменения общего состояния.
---

## Обычный опрос

```kotlin
import io.chengine.dsl.sendTelegramPoll

@HandleCommand("/poll")
fun poll() = sendTelegramPoll {
    question = "Кошки или собаки?"
    options = listOf("Кошки", "Собаки")
    isAnonymous = false
}
```

По умолчанию Telegram создаёт анонимные опросы. Чтобы получать индивидуальные ответы, укажите `isAnonymous = false`.

## Викторина

```kotlin
import io.chengine.dsl.TelegramPollType

@HandleCommand("/quiz")
fun quiz() = sendTelegramPoll {
    question = "Сколько будет 2 + 2?"
    options = listOf("3", "4", "5")
    type = TelegramPollType.QUIZ
    correctOptionIds = listOf(1)
    explanation = "2 + 2 = 4"
    isAnonymous = false
}
```

Индексы правильных ответов начинаются с нуля и задаются по возрастанию. DSL проверяет вопрос, количество и длину вариантов, индексы и часть параметров времени.

## События голосования

Используйте `@TelegramHandlePollAnswer` для индивидуального ответа и `@TelegramHandlePoll` для состояния опроса. Оба обработчика получают исходный `Update`.

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.telegramPollOrigin

@TelegramHandlePollAnswer
fun answer(update: Update, request: BotRequestContext) =
    if (request.telegramPollOrigin() != null) sendTelegramMessage {
        text = if (update.pollAnswer.optionIds.isEmpty()) "Голос отозван" else "Спасибо за ответ!"
    } else Unit
```

`telegramPollOrigin()` возвращает ID опроса, исходный чат и ID сообщения, если связь известна. Если обработчик вернул сообщение без явного `chatId`, Chengine отправит его в исходный чат. Пользователь может отменить голос: тогда `optionIds` пуст. Вместо `user` может присутствовать `voterChat`.

По умолчанию связи опросов хранятся в ограниченном хранилище в памяти и пропадают после перезапуска. Для постоянного хранения реализуйте `TelegramPollOriginStore` и установите его в `ChengineTelegramLongPoolingBot.pollOriginStore` до обработки событий. Для опросов, отправленных вне Chengine, после истечения записи или при гонке во время отправки связь может отсутствовать. Опросы не создают сессии; исходный чат опроса не обязательно является чатом голосующего.

> Индивидуальные ответы приходят только для неанонимных опросов, отправленных ботом. Анонимные опросы дают события об общем состоянии.

[Полный пример]({{ '/examples/#polls' | relative_url }})
