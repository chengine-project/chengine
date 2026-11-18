# Experimental Koog + Telegram example

This example connects Chengine's request-bound Telegram sender to a Koog agent. The LLM receives only the message text parameter; the chat ID comes from the current Chengine request. The integration lives in the `koog` project and is not yet published to Maven Central. Existing Telegram users do not depend on Koog.

Run from a checkout with JDK 17+ and a Telegram bot configured for long polling. Register the bot token and username through environment variables and use an OpenRouter key in `OPENROUTER_API_KEY`:

```bash
export CHENGINE_TELEGRAM_TOKEN='your-bot-token'
export CHENGINE_TELEGRAM_USERNAME='your-bot-username'
export OPENROUTER_API_KEY='your-openrouter-key'
./gradlew :koog:runKoogTelegramDemo
```

Send `/koog` to the bot. The example uses OpenRouter's GPT-4o mini and permits a model call with associated provider costs. The agent may call `send_telegram_message`; otherwise the example sends its final text once. A successful tool send is counted so the final response is not echoed again. Model-selected multiple tool calls are still possible. Do not commit secrets or place them in examples.

Test without network access or API keys:

```bash
./gradlew :koog:test
```

The OpenRouter integration test is skipped unless `CHENGINE_TEST_OPENROUTER=true` and `OPENROUTER_API_KEY` are set. It makes a real model request but uses a fake Telegram client; it does not send a Telegram message.

The example intentionally invokes the agent synchronously on Chengine's request thread and creates an executor per request. This is a bounded demonstration, not a background processing design: Chengine currently uses `ThreadLocal` session context and an in-process per-session lock. Capture the chat ID before entering a coroutine, avoid sharing the tool set across chats, and do not reuse request-scoped session state after the handler returns. There is no persistent model conversation history, distributed locking, duplicate suppression, retry policy, or cross-instance queue in this example. A timeout or retry after a successful send can produce duplicate messages. Treat model outputs as untrusted.

`KoogGraphTest` also demonstrates calling a mocked Koog agent from a synchronous `ConversationGraph` node. It blocks for the duration of the agent call and is not an asynchronous graph implementation. For production use, design an asynchronous job lifecycle and session coordination first.
