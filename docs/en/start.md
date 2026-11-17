---
layout: default
lang: en
title: Quick start
section: GETTING STARTED
permalink: /en/start/
lead: Add Chengine, configure your bot, and create your first handler.
---

## Requirements

- JDK 17 or newer. CI tests JDK 17 and 21.
- Kotlin and Spring Boot; this branch uses Kotlin 2.2.21 and Spring Boot 3.5.16.
- A Telegram bot created with BotFather, its token, and username.

## 1. Get the dependency

Chengine artifacts are distributed through Maven Central. Check the [available starter versions](https://central.sonatype.com/artifact/io.github.chengine-project/spring-boot-starter-telegram) and use a fixed version in the example below. Newly published versions may take some time to become available.

If the release is not yet available or you want the current source, build the library locally:

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

For a local build, add `mavenLocal()` before `mavenCentral()` in `repositories` below. Check the version in the selected commit’s root `build.gradle.kts`.

## 2. Add the starter

In a separate Spring Boot project with JDK 21 installed, add the following to `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.github.chengine-project:spring-boot-starter-telegram:1.0.0")
}

kotlin { jvmToolchain(21) }
```

## 3. Configure the bot

In `src/main/resources/application.yml`:

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
```

Set the environment variables before starting. Do not commit the token to Git.

## 4. Add a handler

```kotlin
import io.chengine.command.HandleCommand
import io.chengine.dsl.sendTelegramMessage
import io.chengine.handler.Handler
import org.springframework.stereotype.Component

@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "Hello!"
    }
}
```

Add an entry point in a parent package of the handler:

```kotlin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

Run the application and send `/hello` to the bot. The starter uses long polling.


## Webhook instead of long polling

Polling is the default. For webhook delivery add `implementation("org.springframework.boot:spring-boot-starter-web")` and configure the application:

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
    mode: webhook
    webhook:
      path: /telegram/webhook
      secret: ${TELEGRAM_WEBHOOK_SECRET}
```

Expose `https://example.com/telegram/webhook` over HTTPS through your server or reverse proxy. Set `TELEGRAM_WEBHOOK_SECRET` (1–256 ASCII letters, digits, `_` or `-`) and register the URL with Telegram after starting the application:

```bash
export TELEGRAM_WEBHOOK_URL=https://example.com/telegram/webhook
curl -fsS "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/setWebhook" \
  --data-urlencode "url=${TELEGRAM_WEBHOOK_URL}" \
  --data-urlencode "secret_token=${TELEGRAM_WEBHOOK_SECRET}"
```

If using Spring Security, permit POST requests to this path and exempt it from CSRF: requests are checked using the secret header. Telegram retries failed deliveries, so handlers must account for possible duplicates. To return to polling, remove the webhook with `deleteWebhook`.

[Next: commands and handlers →]({{ '/en/handlers/' | relative_url }})
