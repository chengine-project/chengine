---
layout: default
title: Примеры целиком
section: ПРИМЕРЫ
permalink: /examples/
lead: Готовые обработчики, которые можно изучить и скопировать прямо из документации.
---

Все примеры рассчитаны на приложение Spring Boot с Telegram-стартером. Создайте проект по [быстрому старту]({{ '/start/' | relative_url }}), затем добавьте **один** обработчик в пакет приложения. У некоторых примеров одинаковые команды или общие обработчики событий, поэтому не включайте их все одновременно без адаптации.

- [Каталог](#catalog) — кнопки, переходы и редактирование сообщения.
- [Диалог](#conversation) — имя и город с графом сценария.
- [Кнопки](#buttons) — callback и ссылка.
- [Редактирование](#editing) — изменение текста и клавиатуры.
- [Медиа](#media) — отправка файлов.
- [Опросы](#polls) — голосование и ответ в исходный чат.
- [Несколько сообщений](#multiple) — последовательные ответы.
- [Клавиатура и видео](#more-dsl) — reply-кнопки и видеосообщение.
- [PostgreSQL](#postgres) — сохранение сессий.
- [Инструмент агента](#agent) — отправка в текущий чат.

## Каталог {#catalog}

Команда `/catalog` открывает каталог с листанием и карточками товаров. Каждый переход обновляет одно сообщение.

<details markdown="1">
<summary>Показать CatalogHandler.kt</summary>
{% highlight kotlin %}{% include examples/CatalogHandler.kt %}{% endhighlight %}
</details>

## Диалог {#conversation}

Команда `/start` начинает диалог об имени и городе; город можно пропустить, а `/cancel` завершает сценарий. Данные по умолчанию хранятся в памяти.

<details markdown="1">
<summary>Показать ConversationHandler.kt</summary>
{% highlight kotlin %}{% include examples/ConversationHandler.kt %}{% endhighlight %}
</details>

## Inline-кнопки {#buttons}

Команда `/start` отправляет кнопку с callback и ссылку на документацию.

<details markdown="1">
<summary>Показать InlineKeyboardHandler.kt</summary>
{% highlight kotlin %}{% include examples/InlineKeyboardHandler.kt %}{% endhighlight %}
</details>

## Редактирование сообщения {#editing}

Отправьте `/edit` и нажмите кнопки, чтобы сначала изменить текст, а затем клавиатуру этого же сообщения.

<details markdown="1">
<summary>Показать EditMessageHandler.kt</summary>
{% highlight kotlin %}{% include examples/EditMessageHandler.kt %}{% endhighlight %}
</details>

## Фото, документы, видео и аудио {#media}

Положите свои `photo.jpg`, `document.pdf`, `video.mp4` и `audio.mp3` в рабочую папку приложения и используйте команды `/photo`, `/document`, `/video` и `/audio`.

<details markdown="1">
<summary>Показать MediaHandler.kt</summary>
{% highlight kotlin %}{% include examples/MediaHandler.kt %}{% endhighlight %}
</details>

## Опрос и викторина {#polls}

Команды `/poll` и `/quiz` создают неанонимные опросы. После голосования бот может ответить в исходном чате, пока связь с опросом доступна.

<details markdown="1">
<summary>Показать PollHandler.kt</summary>
{% highlight kotlin %}{% include examples/PollHandler.kt %}{% endhighlight %}
</details>

Подробнее о настройке хранилища — в разделах [сессии и PostgreSQL]({{ '/sessions/' | relative_url }}) и [опросы]({{ '/polls/' | relative_url }}). [Все исходные примеры](https://github.com/chengine-project/chengine/tree/develop/examples) также доступны в репозитории.

## Несколько сообщений {#multiple}

Обработчик может вернуть список ответов — они будут отправлены по очереди:

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "Первое сообщение" },
    sendTelegramMessage { text = "Второе сообщение" }
)
```

## Клавиатура и видеосообщение {#more-dsl}

```kotlin
sendTelegramVideoNote { videoNote { file = File("note.mp4") } }
sendTelegramMessage {
    text = "Выберите ответ"
    replyKeyboard { row { button { text = "Да" }; button { text = "Нет" } } }
}
sendTelegramMessage { text = "Готово"; replyKeyboard { removeKeyboard() } }
```

Для видеосообщения нужен подходящий квадратный видеофайл. Reply-клавиатура и inline-клавиатура не могут быть прикреплены к одному сообщению.

## Хранение сессий в PostgreSQL {#postgres}

Подключите модуль `session-postgres`, настройте `DataSource` и зарегистрируйте бин:

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

Импорты: `org.springframework.context.annotation.Bean`, `javax.sql.DataSource`, `io.chengine.session.SessionStore`, `io.chengine.session.postgres.PostgresSessionStore`. Подробнее — в [разделе о сессиях]({{ '/sessions/' | relative_url }}).

## Инструмент для агента {#agent}

Экспериментальный инструмент отправляет текст в чат текущего запроса и возвращает ID сообщения. `request` и `client` предоставляет приложение:

```kotlin
fun sendToCurrentChat(request: BotRequestContext, client: TelegramClient): Int =
    TelegramSendMessageTool(request, client).execute("Привет!")
```

Импорты: `io.chengine.connector.BotRequestContext`, `org.telegram.telegrambots.meta.generics.TelegramClient`, `io.chengine.agent.TelegramSendMessageTool`. Реальной модели или автоматического цикла вызова инструментов в этом примере нет. Подробнее — в [разделе о графах]({{ '/graphs/' | relative_url }}).
