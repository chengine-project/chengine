---
layout: default
lang: de
title: Schnellstart
section: GRUNDLAGEN
permalink: /de/start/
lead: Bibliothek lokal bauen, Starter einbinden und den ersten Handler hinzufügen.
---

## Voraussetzungen

- JDK 17 oder neuer. CI testet JDK 17 und 21.
- Kotlin und Spring Boot; dieser Branch verwendet Kotlin 2.2.21 und Spring Boot 3.5.16.
- Ein mit BotFather erstellter Telegram-Bot, sein Token und Benutzername.

## 1. Bibliothek bauen

Diese Anleitung verwendet den aktuellen Quellcode aus einem lokalen Maven-Repository und ist unabhängig von einem veröffentlichten Release.

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

Die Artefaktversion in diesem Branch ist `1.0.0`. Prüfe bei einem anderen Commit `version` in der zentralen Datei `build.gradle.kts`.

## 2. Starter einbinden

Füge in einem separaten Spring-Boot-Projekt mit installiertem JDK 21 Folgendes zu `build.gradle.kts` hinzu:

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
    implementation("io.github.chengine-project:spring-boot-starter-telegram:1.0.0")
}

kotlin { jvmToolchain(21) }
```

## 3. Bot konfigurieren

In `src/main/resources/application.yml`:

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
```

Setze die Umgebungsvariablen vor dem Start. Das Token gehört nicht ins Git-Repository.

## 4. Handler hinzufügen

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
        text = "Hallo!"
    }
}
```

Füge einen Einstiegspunkt in einem übergeordneten Paket des Handlers hinzu:

```kotlin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

Starte die Anwendung und sende dem Bot `/hello`. Der Starter verwendet Long Polling.

[Weiter: Befehle und Handler →]({{ '/de/handlers/' | relative_url }})
