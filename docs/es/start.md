---
layout: default
lang: es
title: Inicio rápido
section: ASPECTOS BÁSICOS
permalink: /es/start/
lead: Añade Chengine, configura el bot y crea tu primer controlador.
---

## Requisitos

- JDK 17 o posterior. CI comprueba JDK 17 y 21.
- Kotlin y Spring Boot; la rama actual usa Kotlin 2.2.21 y Spring Boot 3.5.16.
- Un bot de Telegram creado con BotFather, con su token y nombre de usuario.

## 1. Obtén la dependencia

Los artefactos de Chengine se distribuyen mediante Maven Central. Consulta las [versiones disponibles del starter](https://central.sonatype.com/artifact/io.github.chengine-project/spring-boot-starter-telegram) y usa una versión fija en el ejemplo. Las versiones recién publicadas pueden tardar en estar disponibles.

Si la versión aún no está disponible o quieres usar el código actual, compila la biblioteca localmente:

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

Para una compilación local, añade `mavenLocal()` antes de `mavenCentral()` en `repositories`. Comprueba la versión en `build.gradle.kts` de la raíz del commit elegido.

## 2. Conecta el starter

En un proyecto Spring Boot independiente con JDK 21, añade a `build.gradle.kts`:

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

## 3. Configura el bot

En `src/main/resources/application.yml`:

```yaml
chengine:
  telegram:
    token: ${TELEGRAM_BOT_TOKEN}
    username: ${TELEGRAM_BOT_USERNAME}
```

Define las variables de entorno antes de iniciar la aplicación. No guardes el token en Git.

## 4. Añade un controlador

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
        text = "¡Hola!"
    }
}
```

Añade un punto de entrada en el paquete padre del controlador:

```kotlin
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
```

Inicia la aplicación y envía `/hello` al bot. El starter utiliza long polling.


## Webhook en lugar de long polling

Polling es el modo predeterminado. Para usar webhook, añade `implementation("org.springframework.boot:spring-boot-starter-web")` y configura la aplicación:

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

Expón `https://example.com/telegram/webhook` mediante HTTPS usando el servidor o proxy inverso. Define `TELEGRAM_WEBHOOK_SECRET` (1–256 letras ASCII, dígitos, `_` o `-`) y registra la URL en Telegram después de iniciar la aplicación:

```bash
export TELEGRAM_WEBHOOK_URL=https://example.com/telegram/webhook
curl -fsS "https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/setWebhook" \
  --data-urlencode "url=${TELEGRAM_WEBHOOK_URL}" \
  --data-urlencode "secret_token=${TELEGRAM_WEBHOOK_SECRET}"
```

Con Spring Security, permite POST a esta ruta y exclúyela de CSRF: la cabecera secreta verifica las solicitudes. Telegram reintenta las entregas fallidas; los manejadores deben admitir posibles duplicados. Para volver a polling, elimina el webhook con `deleteWebhook`.

[Siguiente: comandos y controladores →]({{ '/es/handlers/' | relative_url }})
