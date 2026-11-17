---
layout: default
lang: es
title: Inicio rápido
section: ASPECTOS BÁSICOS
permalink: /es/start/
lead: Compila la biblioteca localmente, conecta el starter y añade tu primer controlador.
---

## Requisitos

- JDK 17 o posterior. CI comprueba JDK 17 y 21.
- Kotlin y Spring Boot; la rama actual usa Kotlin 2.2.21 y Spring Boot 3.5.16.
- Un bot de Telegram creado con BotFather, con su token y nombre de usuario.

## 1. Compila la biblioteca

El ejemplo conecta el código actual desde un repositorio Maven local y no depende de una versión publicada.

```bash
git clone https://github.com/chengine-project/chengine.git
cd chengine
./gradlew publishToMavenLocal
```

La versión de los artefactos en esta rama es `1.0.0`. Si usas otro commit, comprueba `version` en el archivo raíz `build.gradle.kts`.

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
    mavenLocal()
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

[Siguiente: comandos y controladores →]({{ '/es/handlers/' | relative_url }})
