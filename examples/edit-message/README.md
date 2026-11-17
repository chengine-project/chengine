# Editing messages and inline keyboards

Copy `EditMessageHandler.kt` into a Spring Boot Telegram bot. Send `/edit`, press the first button to change its text, then the next button to replace its keyboard with a documentation link. The callback is acknowledged before each edit.

`editTelegramMessageText` edits a message's text and optionally supplies a replacement inline keyboard. `editTelegramMessageReplyMarkup` replaces only the keyboard; use `removeKeyboard()` to remove it. Supply a positive `messageId`, and optionally a `chatId` (otherwise Chengine derives the chat from the incoming update). For inline-mode messages, supply `inlineMessageId` instead of chat and message IDs. Telegram may reject edits for messages it does not allow the bot to edit, unchanged content, or invalid targets.

To construct a modified copy of an existing inline keyboard, use `editInlineKeyboardMarkup(original) { ... }` with `editButton`, `deleteRow`, or `deleteKeyboard`, then assign the result to a Telegram `EditMessageReplyMarkup` request. The original keyboard is not mutated.
