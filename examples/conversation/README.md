# Conversation example

Copy `ConversationHandler.kt` into a Spring Boot application using the Telegram starter. Send `/start`, then your name and city. `/cancel` ends the conversation. State expires after ten minutes of inactivity. The example handles text, not media.

`Conversations.start` stores a `ConversationSession`, `Conversations.current` reads it within the handler, and `Conversations.end` removes it. Sessions are keyed by platform, bot account, user, and chat. Telegram uses the configured username, case-insensitively, as its bot account identifier. Changing the username changes the key. Other adapters should override `sessionBotIdentifier()` with a stable, non-secret account identifier; the default is only stable for the lifetime of the bot instance.

By default sessions are held in the current process and lost on restart. To use another backend, implement `SessionStore` and register it as a Spring bean, or supply it via `ChengineConfig.sessionStore`. `load` must renew the inactivity timeout, `save` must replace and renew the state, and `delete` must remove it. `ConversationSession.values()` and its constructor's `initialValues` support mapping conversation state to a storage representation. The example backend contract does not prescribe a format for experimental `PipelineState`.

Requests for the same session are serialized, including sending the response, within a single JVM. Requests for different sessions may run concurrently; simultaneously arriving requests have no guaranteed arrival order. A multi-process deployment needs its own distributed coordination. Exceptions do not provide transactional rollback. `Pipeline` remains experimental and is not used here.
