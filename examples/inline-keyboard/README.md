# Inline keyboard example

Copy `InlineKeyboardHandler.kt` into a Spring Boot application using the Chengine Telegram starter. Configure `chengine.telegram.token` and `chengine.telegram.username` as described in the main README. Send `/start`, then press the button: the bot acknowledges the callback with a Telegram alert.

`@TelegramHandleCallback` handles callback events without a registered command. Callback data that matches a registered `@HandleCommand` is routed to that command first. The callback must be answered with `AnswerCallbackQuery` to stop the client's progress indicator.
