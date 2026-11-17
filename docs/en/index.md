---
layout: default
lang: en
title: Build bots more easily
section: OVERVIEW
permalink: /en/
lead: Chengine is a Kotlin framework for Telegram bots with familiar handlers and Spring Boot integration.
---

From your first command to a multi-step conversation: describe your bot in Kotlin, and Chengine finds the handler and sends its response to Telegram.

<a class="button" href="{{ '/en/start/' | relative_url }}">Get started →</a><a class="button secondary" href="https://github.com/chengine-project/chengine">View on GitHub ↗</a>

## What's inside

<div class="cards">
  <a class="card" href="{{ '/en/handlers/' | relative_url }}"><strong>Commands and text →</strong><span>Route incoming events to methods.</span></a>
  <a class="card" href="{{ '/en/buttons/' | relative_url }}"><strong>Buttons and callbacks →</strong><span>Create keyboards and handle button presses.</span></a>
  <a class="card" href="{{ '/en/media/' | relative_url }}"><strong>Media and files →</strong><span>Receive and send photos, files, and video.</span></a>
  <a class="card" href="{{ '/en/sessions/' | relative_url }}"><strong>Sessions and conversations →</strong><span>Store state in memory or PostgreSQL.</span></a>
  <a class="card" href="{{ '/en/polls/' | relative_url }}"><strong>Polls →</strong><span>Run polls and process answers.</span></a>
  <a class="card" href="{{ '/en/graphs/' | relative_url }}"><strong>Graphs →</strong><span>Build flows with transitions and waiting.</span></a>
  <a class="card" href="{{ '/en/examples/' | relative_url }}"><strong>Complete examples →</strong><span>Ready-to-use handlers in the documentation.</span></a>
</div>

## Quick example

```kotlin
@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "Hello!"
    }
}
```

> This documentation describes the `develop` branch. The API is evolving and some features are experimental.
