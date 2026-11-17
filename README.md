<div align="center">
  <img src="docs/assets/readme-hero-v2.png" alt="Chengine — build Telegram bots with Kotlin" width="100%">
  <p><strong>Commands, buttons, and conversations in a familiar Kotlin DSL.</strong></p>
  <p>
    <a href="https://chengine-project.github.io/chengine/en/">Documentation</a> &nbsp;·&nbsp;
    <a href="https://chengine-project.github.io/chengine/en/start/">Quick start</a> &nbsp;·&nbsp;
    <a href="https://chengine-project.github.io/chengine/en/examples/">Examples</a> &nbsp;·&nbsp;
    <a href="LICENSE">MIT License</a>
  </p>
</div>

---

Chengine is a Kotlin framework for Telegram bots with Spring Boot. Define a handler and return a response; the framework routes the event to the appropriate method and sends the result through the Telegram Bot API.

```kotlin
@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "Hello! I'm powered by Chengine 👋"
    }
}
```

## Features

| Feature | What it does |
| --- | --- |
| **Commands and events** | Handle commands, text, callbacks, media, and polls |
| **Keyboards** | Use inline buttons, links, reply keyboards, and message editing |
| **Media** | Send photos, documents, video, audio, stickers, and animations |
| **Conversations** | Keep session state and user data in memory or PostgreSQL |
| **Graphs** | Build experimental flows with branching and waiting for replies |

## Quick start

You will need JDK 17 or newer, Kotlin and Spring Boot, and a Telegram bot token and username. A public Maven release is not yet configured, so first build the library into your local Maven repository:

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

In a separate Spring Boot project, add `mavenLocal()` and the dependency:

```kotlin
repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("io.chengine:spring-boot-starter-telegram:1.0.1-BETA")
}
```

Configure the application using environment variables:

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
```

Add the handler shown above, with imports for `io.chengine.command.HandleCommand`, `io.chengine.dsl.sendTelegramMessage`, `io.chengine.handler.Handler`, and `org.springframework.stereotype.Component`. Start your Spring Boot application and send `/hello` to the bot.

**[Complete quick start →](https://chengine-project.github.io/chengine/en/start/)**

## Explore the examples

| Example | What it shows |
| --- | --- |
| [Catalog](https://chengine-project.github.io/chengine/en/examples/#catalog) | Multiple button rows, pages, and updates to a single message |
| [Conversation](https://chengine-project.github.io/chengine/en/examples/#conversation) | Name and city, conditional transitions, and cancellation |
| [Media](https://chengine-project.github.io/chengine/en/examples/#media) | Sending files and handling incoming media |
| [Polls](https://chengine-project.github.io/chengine/en/examples/#polls) | Quizzes, answers, and the original poll chat |
| [PostgreSQL](https://chengine-project.github.io/chengine/en/sessions/#postgresql) | Persisting sessions across restarts |
| [More examples](https://chengine-project.github.io/chengine/en/examples/) | Message editing, keyboards, and an agent tool |

## Build and test

```bash
./gradlew build
```

CI checks JDK 17 and 21, and PostgreSQL integration tests run against a dedicated database. See the [local verification and release preparation guide](https://chengine-project.github.io/chengine/en/releasing/).

The API is evolving. Conversation graphs and the agent tool are experimental; there is no integration with a specific LLM yet. Persistent session storage does not coordinate concurrent work across multiple application instances.

---

[Documentation](https://chengine-project.github.io/chengine/en/) · [Source code](https://github.com/chengine-project/chengine) · [MIT License](LICENSE)
