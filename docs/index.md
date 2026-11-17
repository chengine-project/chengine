---
layout: default
title: Создавайте ботов проще
section: ОБЗОР
lead: Chengine — Kotlin-фреймворк для Telegram-ботов со знакомой моделью обработчиков и интеграцией со Spring Boot.
---

От первой команды до многошагового диалога: опишите поведение бота в Kotlin, а Chengine найдёт обработчик и передаст ответ Telegram.

<a class="button" href="{{ '/start/' | relative_url }}">Начать работу →</a><a class="button secondary" href="https://github.com/chengine-project/chengine">Открыть на GitHub ↗</a>

## Что внутри

<div class="cards">
  <a class="card" href="{{ '/handlers/' | relative_url }}"><strong>Команды и текст →</strong><span>Привяжите входящие события к методам.</span></a>
  <a class="card" href="{{ '/buttons/' | relative_url }}"><strong>Кнопки и callback →</strong><span>Создавайте клавиатуры и отвечайте на нажатия.</span></a>
  <a class="card" href="{{ '/media/' | relative_url }}"><strong>Медиа и файлы →</strong><span>Принимайте и отправляйте фото, файлы и видео.</span></a>
  <a class="card" href="{{ '/sessions/' | relative_url }}"><strong>Сессии и диалоги →</strong><span>Храните состояние в памяти или PostgreSQL.</span></a>
  <a class="card" href="{{ '/polls/' | relative_url }}"><strong>Опросы →</strong><span>Проводите голосования и обрабатывайте ответы.</span></a>
  <a class="card" href="{{ '/graphs/' | relative_url }}"><strong>Графы →</strong><span>Стройте сценарии с переходами и ожиданием.</span></a>
</div>

## Быстрый пример

```kotlin
@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "Привет!"
    }
}
```

> Документация описывает ветку `develop`. API проекта пока развивается; отдельные возможности остаются экспериментальными.
