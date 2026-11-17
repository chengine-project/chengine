---
layout: default
title: Команды и обработчики
section: ОСНОВНОЕ
permalink: /handlers/
lead: Объявляйте методы, которые вызываются по команде, тексту или типу события Telegram.
---

## Команды

Пометьте Spring-компонент аннотацией `@Handler`, а метод — `@HandleCommand`. Верните Telegram-метод, например результат `sendTelegramMessage`.

```kotlin
@Handler
@Component
class MenuHandler {
    @HandleCommand("/help")
    fun help() = sendTelegramMessage {
        text = "Чем я могу помочь?"
    }
}
```

Для групп поддерживается обращение `/help@bot_username`. Аргументы после пробела не входят в путь команды; полный текст остаётся в `Update`.

```kotlin
@HandleCommand("/start")
fun start(update: Update) = sendTelegramMessage {
    text = "Получено: ${update.message.text}"
}
```

Добавьте импорт `org.telegram.telegrambots.meta.api.objects.Update`. Неизвестные и некорректные команды игнорируются; ошибки регистрации и дублирующиеся пути обнаруживаются при запуске.

## Точное совпадение текста

```kotlin
@HandleText("Помощь")
fun helpText() = sendTelegramMessage { text = "Справка" }
```

Аннотация находится в `io.chengine.text.HandleText`. Для прочего текста можно использовать `@TelegramHandleText` из `io.chengine.handler`.

## Приоритет обработчиков

Сначала ищется команда, затем точное совпадение текста, затем общий обработчик типа события. Для command-пути Chengine также поддерживается формат `/item/id#42` с параметром `@CommandParameter("id")`.

[Пример диалога →]({{ '/sessions/' | relative_url }})
