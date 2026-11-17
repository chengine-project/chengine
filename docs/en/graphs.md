---
layout: default
lang: en
title: Graphs and experiments
section: FEATURES
permalink: /en/graphs/
lead: Build a conversation from nodes, transitions, and waiting for the next message.
---

`ConversationGraph` is an experimental synchronous graph built on `ConversationSession`. A node receives state and incoming text, then returns one of these transitions:

- `Next("node")` runs the next node immediately;
- `Await("node", "message")` returns a message and waits for new text for the specified node;
- `Finish("message")` completes the flow.

```kotlin
val graph = ConversationGraph("start", mapOf(
    "start" to { _, _ -> GraphTransition.Await("name", "What is your name?") },
    "name" to { session, input ->
        session["name"] = input.orEmpty()
        GraphTransition.Next("finish")
    },
    "finish" to { session, _ -> GraphTransition.Finish("Hello, ${session["name"]}!") }
))
```

In a handler, create a session with `Conversations.start`, call `graph.start(session)`, and return the result message. On the next message call `graph.resume(session, text)`. If `result.completed` is `true`, end the session with `Conversations.end()`. Pass data between automatic nodes through the session: input text is not forwarded by `Next`. A transition limit stops infinite loops.

[Complete example with name, city, and branching]({{ '/en/examples/#conversation' | relative_url }}).

## Agent tool

The experimental `TelegramSendMessageTool` binds to the current request's chat and sends text through the supplied Telegram client. The model supplies text and the chat comes from the context. The project includes a test with a fake model; there is no ready-made LLM adapter or agent loop yet. [Description and limits](https://github.com/chengine-project/chengine/tree/develop/examples/agent-tool).
