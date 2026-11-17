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
@TelegramHandlePollAnswer
fun answer(update: Update) {
    val answer = update.pollAnswer
    println("Опрос ${answer.pollId}: ${answer.optionIds}")
}
```

Пользователь может отменить голос: тогда `optionIds` пуст. Вместо `user` может присутствовать `voterChat`.

> Индивидуальные ответы приходят только для неанонимных опросов, отправленных ботом. События опроса не содержат ID исходного чата: если он нужен для ответа, сохраняйте связь `pollId → chatId` в своём приложении.

[Полный пример в репозитории](https://github.com/chengine-project/chengine/tree/develop/examples/polls)
