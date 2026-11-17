---
layout: default
title: Графы и эксперименты
section: ВОЗМОЖНОСТИ
permalink: /graphs/
lead: Стройте диалог из узлов, переходов и ожидания следующего сообщения.
---

`ConversationGraph` — экспериментальный синхронный граф поверх `ConversationSession`. Узел получает состояние и входящий текст, затем возвращает один из переходов:

- `Next("node")` — сразу выполнить следующий узел;
- `Await("node", "сообщение")` — вернуть сообщение и ждать новый текст для указанного узла;
- `Finish("сообщение")` — завершить сценарий.

```kotlin
val graph = ConversationGraph("start", mapOf(
    "start" to { _, _ -> GraphTransition.Await("name", "Как вас зовут?") },
    "name" to { session, input ->
        session["name"] = input.orEmpty()
        GraphTransition.Next("finish")
    },
    "finish" to { session, _ -> GraphTransition.Finish("Привет, ${session["name"]}!") }
))
```

В обработчике создайте сессию через `Conversations.start`, вызовите `graph.start(session)` и верните текст результата. Для следующего сообщения вызовите `graph.resume(session, text)`. Если `result.completed` равен `true`, завершите сессию через `Conversations.end()`. Передавайте данные между автоматическими узлами через сессию: входной текст при `Next` не передаётся. Число переходов ограничено, чтобы остановить бесконечный цикл.

[Полный пример с именем, городом и ветвлением](https://github.com/chengine-project/chengine/tree/develop/examples/conversation).

## Инструмент для агента

Экспериментальный `TelegramSendMessageTool` привязывается к чату текущего запроса и позволяет вызвать отправку текста через переданный Telegram-клиент. Модель передаёт текст, а чат берётся из контекста. В проекте есть тест с поддельной моделью; готового адаптера к LLM или цикла агента пока нет. [Описание и ограничения](https://github.com/chengine-project/chengine/tree/develop/examples/agent-tool).
