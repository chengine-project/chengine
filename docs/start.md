---
layout: default
title: Быстрый старт
section: ОСНОВНОЕ
permalink: /start/
lead: Соберите библиотеку локально, подключите стартер и добавьте первый обработчик.
---

## Требования

- JDK 17 или новее. CI проверяет JDK 17 и 21.
- Kotlin и Spring Boot; текущая ветка использует Kotlin 2.2.21 и Spring Boot 3.5.16.
- Telegram-бот, созданный через BotFather, его токен и имя.

## 1. Соберите библиотеку

Ниже показано подключение текущего исходного кода из локального Maven-репозитория. Оно не зависит от наличия опубликованного релиза.

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

В этой ветке версия артефактов — `1.0.1-BETA`. Проверьте `version` в корневом `build.gradle.kts`, если используете другой коммит.

## 2. Подключите стартер

В отдельном Spring Boot-проекте с установленной JDK 21 добавьте в `build.gradle.kts`:

```kotlin
plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "3.5.16"
    id("io.spring.dependency-management") version "1.1.7"
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.github.chengine-project:spring-boot-starter-telegram:1.0.1-BETA")
}

kotlin { jvmToolchain(21) }
```

## 3. Настройте бота

В `src/main/resources/application.yml`:

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
```

Задайте переменные окружения перед запуском. Токен не нужно добавлять в Git.

## 4. Добавьте обработчик

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
        text = "Привет!"
    }
}
```

Добавьте точку входа в родительском пакете обработчика:

```kotlin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

Запустите приложение и отправьте боту `/hello`. Стартер использует long polling.

[Далее: команды и обработчики →]({{ '/handlers/' | relative_url }})
