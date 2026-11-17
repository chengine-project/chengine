---
layout: default
lang: de
title: Schnellstart
section: GRUNDLAGEN
permalink: /de/start/
lead: Chengine einbinden, den Bot konfigurieren und den ersten Handler erstellen.
---

## Voraussetzungen

- JDK 17 oder neuer. CI testet JDK 17 und 21.
- Kotlin und Spring Boot; dieser Branch verwendet Kotlin 2.2.21 und Spring Boot 3.5.16.
- Ein mit BotFather erstellter Telegram-Bot, sein Token und Benutzername.

## 1. Abhängigkeit beziehen

Chengine-Artefakte werden über Maven Central verteilt. Prüfe die [verfügbaren Starter-Versionen](https://central.sonatype.com/artifact/io.github.chengine-project/spring-boot-starter-telegram) und verwende im Beispiel eine feste Version. Neu veröffentlichte Versionen sind möglicherweise nicht sofort verfügbar.

Wenn das Release noch nicht verfügbar ist oder du den aktuellen Quellcode verwenden möchtest, baue die Bibliothek lokal:

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

Füge für einen lokalen Build `mavenLocal()` vor `mavenCentral()` in `repositories` hinzu. Prüfe die Version in der `build.gradle.kts` im Stammverzeichnis des gewählten Commits.

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


## Webhook statt Long Polling

Polling ist die Voreinstellung. Für Webhooks füge `implementation("org.springframework.boot:spring-boot-starter-web")` hinzu und konfiguriere die Anwendung:

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

Stelle `https://example.com/telegram/webhook` über HTTPS mit einem Server oder Reverse-Proxy bereit. Setze `TELEGRAM_WEBHOOK_SECRET` (1–256 ASCII-Buchstaben, Ziffern, `_` oder `-`) und registriere nach dem Start die URL bei Telegram:

```bash
export TELEGRAM_WEBHOOK_URL=https://example.com/telegram/webhook
curl -fsS "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/setWebhook" \
  --data-urlencode "url=${TELEGRAM_WEBHOOK_URL}" \
  --data-urlencode "secret_token=${TELEGRAM_WEBHOOK_SECRET}"
```

Erlaube bei Spring Security POST-Anfragen an diesen Pfad und nimm ihn von CSRF aus: Der geheime Header prüft die Anfrage. Telegram wiederholt fehlgeschlagene Zustellungen; Handler müssen mögliche Duplikate berücksichtigen. Für die Rückkehr zu Polling den Webhook mit `deleteWebhook` entfernen.

[Weiter: Befehle und Handler →]({{ '/de/handlers/' | relative_url }})
