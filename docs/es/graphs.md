---
layout: default
lang: es
title: Grafos y experimentos
section: FUNCIONALIDADES
permalink: /es/graphs/
lead: Crea diálogos con nodos, transiciones y espera del siguiente mensaje.
---

`ConversationGraph` es un grafo síncrono experimental sobre `ConversationSession`. Un nodo recibe el estado y el texto entrante y devuelve una transición:

- `Next("node")`: ejecuta el siguiente nodo inmediatamente;
- `Await("node", "mensaje")`: devuelve un mensaje y espera un nuevo texto para el nodo indicado;
- `Finish("mensaje")`: termina el flujo.

```kotlin
val graph = ConversationGraph("start", mapOf(
    "start" to { _, _ -> GraphTransition.Await("name", "¿Cómo te llamas?") },
    "name" to { session, input ->
        session["name"] = input.orEmpty()
        GraphTransition.Next("finish")
    },
    "finish" to { session, _ -> GraphTransition.Finish("¡Hola, ${session["name"]}!") }
))
```

En el controlador, crea una sesión con `Conversations.start`, llama a `graph.start(session)` y devuelve el texto del resultado. Para el siguiente mensaje llama a `graph.resume(session, text)`. Si `result.completed` es `true`, termina la sesión con `Conversations.end()`. Comparte datos entre nodos automáticos mediante la sesión: `Next` no reenvía el texto entrante. El número de transiciones está limitado para evitar ciclos infinitos.

[Ejemplo completo con nombre, ciudad y bifurcación]({{ '/es/examples/#conversation' | relative_url }}).

## Herramienta para agentes

El `TelegramSendMessageTool` experimental se vincula al chat de la solicitud actual y envía texto mediante el cliente de Telegram proporcionado. El modelo aporta el texto y el chat se obtiene del contexto. La herramienta por sí sola no incluye una integración LLM ni un ciclo de agente. [Descripción y límites](https://github.com/chengine-project/chengine/tree/main/examples/agent-tool).

Para una integración experimental con Koog, una herramienta de Telegram y OpenRouter, consulta el [ejemplo y las instrucciones](https://github.com/chengine-project/chengine/tree/main/examples/koog-telegram).
