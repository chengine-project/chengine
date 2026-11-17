---
layout: default
lang: es
title: Crea bots de forma más sencilla
section: PRESENTACIÓN
permalink: /es/
lead: Chengine es un framework de Kotlin para bots de Telegram con un modelo de controladores familiar e integración con Spring Boot.
---

Desde el primer comando hasta un diálogo de varios pasos: describe el comportamiento del bot en Kotlin y Chengine encontrará el controlador y enviará la respuesta a Telegram.

<a class="button" href="{{ '/es/start/' | relative_url }}">Empezar →</a><a class="button secondary" href="https://github.com/chengine-project/chengine">Abrir en GitHub ↗</a>

## Funcionalidades

<div class="cards">
  <a class="card" href="{{ '/es/handlers/' | relative_url }}"><strong>Comandos y texto →</strong><span>Asocia los eventos entrantes a métodos.</span></a>
  <a class="card" href="{{ '/es/buttons/' | relative_url }}"><strong>Botones y callbacks →</strong><span>Crea teclados y responde a las pulsaciones.</span></a>
  <a class="card" href="{{ '/es/media/' | relative_url }}"><strong>Multimedia y archivos →</strong><span>Recibe y envía fotos, archivos y vídeos.</span></a>
  <a class="card" href="{{ '/es/sessions/' | relative_url }}"><strong>Sesiones y diálogos →</strong><span>Guarda el estado en memoria o PostgreSQL.</span></a>
  <a class="card" href="{{ '/es/polls/' | relative_url }}"><strong>Encuestas →</strong><span>Crea votaciones y procesa respuestas.</span></a>
  <a class="card" href="{{ '/es/graphs/' | relative_url }}"><strong>Grafos →</strong><span>Crea flujos con transiciones y pausas.</span></a>
  <a class="card" href="{{ '/es/examples/' | relative_url }}"><strong>Ejemplos completos →</strong><span>Manejadores completos en la documentación.</span></a>
</div>

## Ejemplo rápido

```kotlin
@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "¡Hola!"
    }
}
```

> Esta documentación describe la rama `develop`. La API está en evolución y algunas funciones son experimentales.
