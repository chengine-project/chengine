# Inline keyboard example

Copy `InlineKeyboardHandler.kt` into a Spring Boot application using the Chengine Telegram starter. Configure `chengine.telegram.token` and `chengine.telegram.username` as described in the main README. Send `/start`: one button triggers a callback alert and the other opens the documentation URL.

`@TelegramHandleCallback` handles callback events without a registered command. Callback data that matches a registered `@HandleCommand` is routed to that command first. The callback must be answered with `AnswerCallbackQuery` to stop the client's progress indicator.

Each inline button must specify exactly one action: `callbackData`, `url`, `switchInlineQuery`, `switchInlineQueryCurrentChat`, or `pay = true`. Callback data must contain 1–64 UTF-8 bytes. An empty inline query is allowed. Telegram imposes additional rules on particular button types, such as payment buttons.
