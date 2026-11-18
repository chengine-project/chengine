# Experimental Koog + Telegram example

This example connects Chengine's request-bound Telegram sender to a Koog agent. The LLM receives only the message text parameter; the chat ID comes from the current Chengine request. The integration lives in the `koog` project and is not yet published to Maven Central. Existing Telegram users do not depend on Koog.

Run from a checkout with JDK 17+ and a Telegram bot configured for long polling. Register the bot token and username through environment variables and use an OpenRouter key in `OPENROUTER_API_KEY`:

```bash
export CHENGINE_TELEGRAM_TOKEN='your-bot-token'
export CHENGINE_TELEGRAM_USERNAME='your-bot-username'
export OPENROUTER_API_KEY='your-openrouter-key'
./gradlew :koog:runKoogTelegramDemo
```

Send `/koog` to the bot. The example uses OpenRouter's GPT-4o mini and permits a model call with associated provider costs. The agent may call `send_telegram_message`; otherwise the example sends its final text once. The example sets `maxMessages = 1` and counts successful sends, preventing further successful tool sends and a duplicate final response. Do not commit secrets or place them in examples.

Test without network access or API keys:

```bash
./gradlew :koog:test
```

The OpenRouter integration test is skipped unless `CHENGINE_TEST_OPENROUTER=true` and `OPENROUTER_API_KEY` are set. It makes a real model request but uses a fake Telegram client; it does not send a Telegram message.

The example uses `AsyncConversationGraph` and `koogAgentNode` to invoke Koog in a coroutine without blocking the Telegram request thread. `TelegramTools` runs the blocking Telegram client on `Dispatchers.IO`. The handler captures the request-bound sender and immutable session key before launching a lifecycle-bound coroutine; it does not access Chengine's thread-local session context from the coroutine. A second `/koog` request for the same session is ignored while the first is in progress. Other sessions can proceed independently. The sample logs failures; it does not send an error message or retry automatically.

For a graph that waits for a message before invoking the agent:

```kotlin
val graph = AsyncConversationGraph("ask", mapOf(
    "ask" to { _, _ -> GraphTransition.Await("agent", "What is your name?") },
    "agent" to koogAgentNode(agent, timeoutMillis = 30_000),
))

// Call from a managed coroutine; keep the session explicitly in application-owned storage.
val question = graph.start(session)
// When the next message arrives, load that session and resume from a managed coroutine.
val answer = graph.resume(session, text)
```

The graph itself neither loads nor persists sessions and does not synchronize concurrent access to the same session: the caller owns session lifecycle, persistence, and concurrency. The example's single-flight guard is local to one process and its graph session is temporary. There is no persistent model conversation history, distributed locking, durable queue, or retry policy. Cancellation or timeout does not guarantee that a Telegram request already in progress was cancelled or unsent; retries can produce duplicates. Node side effects are not rolled back on cancellation.

`KoogGraphTest` tests the graph with a mocked Koog agent, including actual suspension and cancellation. The optional OpenRouter test uses the async graph with a real model and a fake Telegram client.
