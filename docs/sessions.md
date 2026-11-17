---
layout: default
title: Сессии и диалоги
section: ВОЗМОЖНОСТИ
permalink: /sessions/
lead: Сохраняйте шаг и данные пользователя между сообщениями.
---

## Начните диалог

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.session.Conversations

@HandleCommand("/start")
fun start(request: BotRequestContext) = sendTelegramMessage {
    Conversations.start(request, "name")
    text = "Как вас зовут?"
}
```

Для создания сессии входящее событие должно содержать пользователя и чат. Доступность `request.sessionKey()` можно проверить до вызова `start`.

## Обработайте ответ

```kotlin
@TelegramHandleText
fun answer(request: BotRequestContext) = sendTelegramMessage {
    val session = Conversations.current()
    val answer = request.textContent()?.trim().orEmpty()
    text = when {
        session == null -> "Начните с /start"
        session.step == "name" -> {
            session["name"] = answer
            session.step = "city"
            "В каком вы городе?"
        }
        session.step == "city" -> {
            val name = session["name"]
            Conversations.end()
            "Приятно познакомиться, $name! Ваш город: $answer."
        }
        else -> "Начните с /start"
    }
}
```

Для отмены вызовите `Conversations.end()` в обработчике `/cancel`. Это пример прямой работы с сессией; [полный пример](https://github.com/chengine-project/chengine/tree/develop/examples/conversation) использует граф, проверяет пустой ответ и отсутствие ключа сессии.

## Хранение и ограничения

По умолчанию сессии хранятся в памяти и исчезают после перезапуска. Время неактивности по умолчанию — 10 минут; его можно изменить аргументами `timeout` и `unit` при вызове `Conversations.start`.

Ключ включает платформу, аккаунт бота, пользователя и чат. Для Telegram используется настроенное имя бота, поэтому его изменение меняет ключи. Запросы одной сессии обрабатываются последовательно в пределах JVM; порядок одновременно поступивших сообщений не гарантирован.

## PostgreSQL

Модуль `session-postgres` сохраняет `ConversationSession` между перезапусками. Добавьте зависимость `io.chengine:session-postgres:1.0.1-BETA`, настройте PostgreSQL `DataSource` и зарегистрируйте бин:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Импорты: `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`, `javax.sql.DataSource` и `org.springframework.context.annotation.Bean`. В рабочем приложении таблицу также можно создавать отдельно при настройке схемы базы. Для удаления истёкших записей предусмотрен `deleteExpired()`. Хранилище работает с `ConversationSession`; интерфейс `SessionStore` позволяет реализовать другое хранилище.

PostgreSQL сохраняет состояние, но не синхронизирует несколько процессов: для них нужна отдельная координация. Сохранение и отправка сообщения не образуют единую транзакцию.

## Граф сценария

Экспериментальный `ConversationGraph` добавляет именованные узлы, условные переходы и ожидание следующего ответа поверх той же сессии. [Как устроены графы →]({{ '/graphs/' | relative_url }})

[Полный пример в репозитории](https://github.com/chengine-project/chengine/tree/develop/examples/conversation)
