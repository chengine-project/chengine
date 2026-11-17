---
layout: default
title: Быстрый старт
section: ОСНОВНОЕ
permalink: /start/
lead: Подключите Chengine, настройте бота и добавьте первый обработчик.
---

## Требования

- JDK 17 или новее. CI проверяет JDK 17 и 21.
- Kotlin и Spring Boot; текущая ветка использует Kotlin 2.2.21 и Spring Boot 3.5.16.
- Telegram-бот, созданный через BotFather, его токен и имя.

## 1. Получите зависимость

Артефакты Chengine распространяются через Maven Central. Проверьте [доступные версии стартера](https://central.sonatype.com/artifact/io.github.chengine-project/spring-boot-starter-telegram) и укажите нужную фиксированную версию в примере ниже. После публикации новой версии её появление в репозитории может занять некоторое время.

Если релиз ещё недоступен или вы хотите использовать текущий исходный код, соберите библиотеку локально:

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

Для локальной сборки добавьте `mavenLocal()` перед `mavenCentral()` в `repositories` ниже. Версию проверяйте в корневом `build.gradle.kts` выбранного коммита.

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
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("io.github.chengine-project:spring-boot-starter-telegram:1.0.0")
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


## Webhook вместо long polling

По умолчанию используется polling. Для webhook добавьте в зависимости `implementation("org.springframework.boot:spring-boot-starter-web")` и настройте приложение:

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

Опубликуйте HTTPS-адрес `https://example.com/telegram/webhook` через сервер или обратный прокси. Задайте `TELEGRAM_WEBHOOK_SECRET` (1–256 латинских букв, цифр, `_` или `-`) и зарегистрируйте URL в Telegram после запуска приложения:

```bash
export TELEGRAM_WEBHOOK_URL=https://example.com/telegram/webhook
curl -fsS "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/setWebhook" \
  --data-urlencode "url=${TELEGRAM_WEBHOOK_URL}" \
  --data-urlencode "secret_token=${TELEGRAM_WEBHOOK_SECRET}"
```

Если используется Spring Security, разрешите POST на этот путь и исключите его из CSRF-проверки: запрос проверяется по секретному заголовку. Telegram повторяет доставку при ответе с ошибкой, поэтому обработчики должны учитывать возможные повторы. Для возврата к polling удалите webhook через `deleteWebhook`.

[Далее: команды и обработчики →]({{ '/handlers/' | relative_url }})
