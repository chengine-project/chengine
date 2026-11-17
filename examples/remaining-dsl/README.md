# Video notes, venues and keyboards

```kotlin
sendTelegramVideoNote { videoNote { file = File("note.mp4") } }
sendTelegramVenue { latitude = 55.75; longitude = 37.61; title = "Место"; address = "Москва" }
sendTelegramMessage {
    text = "Выберите действие"
    replyKeyboard { row { button { text = "Да" }; button { text = "Нет" } } }
}
sendTelegramMessage { text = "Готово"; replyKeyboard { removeKeyboard() } }
```

`videoNote` accepts a file, named stream, or Telegram file ID; Telegram requires a suitable square video. `editTelegramMessageLiveLocation` edits a still-active live location, accepting `messageId` (with optional `chatId`) or `inlineMessageId`, new coordinates, and an optional inline keyboard. An inline keyboard and a reply keyboard cannot both be attached to one message. Reply keyboards are currently supported by `sendTelegramMessage`.

To modify an existing inline keyboard, `editInlineKeyboardMarkup(original)` returns a copy and supports changing button text or action, deleting a row, or clearing the keyboard. Pass the result as `replyMarkup` to a Telegram `EditMessageReplyMarkup` request; changes do not mutate the original keyboard.
