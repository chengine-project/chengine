# Photos, documents, video, and audio

Copy `MediaHandler.kt` into a Spring Boot application using the Telegram starter and supply your own `photo.jpg`, `document.pdf`, `video.mp4`, and `audio.mp3`. The handler's chat ID is filled from the incoming update unless an explicit `chatId` is supplied.

All four builders accept a Telegram `file_id` or URL with `photo { name = "..." }`, `document { name = "..." }`, `video { name = "..." }`, or `audio { name = "..." }`, a `File` with `file = File("...")`, or an `InputStream` with `mediaStream = stream; name = "filename.ext"`. They also accept a direct Telegram `InputFile` through the `photo`, `document`, `video`, or `audio` property. Do not reuse a consumed stream for repeated sends.

## Receiving media

Use `@TelegramHandleMedia` on one handler method and receive the original Telegram `Update` as an argument. For example, `update.message.photo`, `.document`, `.video`, `.audio`, `.voice`, `.animation`, `.videoNote`, and `.sticker` contain the incoming media; `update.message.caption` contains an optional caption. Only ordinary incoming messages are routed this way; edited messages and channel posts are not covered. Captions are not treated as text commands. Retrieve the file through the Telegram API using its `fileId` when needed.

```kotlin
@TelegramHandleMedia
fun receive(update: Update) = sendTelegramMessage {
    text = if (update.message.photo != null) "Получено фото!" else "Получен файл!"
}
```

Long-polling reports exceptions per update and continues with the rest of a batch. Override `TelegramBotsLongPoolingBot.onUpdateError` to integrate application logging or monitoring. Cancellation, interruption, and JVM errors propagate. Failed updates are not automatically retried; side effects performed before an error are not rolled back.

## Stickers, animations, and media edits

`sendTelegramSticker { sticker { name = "file_id" } }` and `sendTelegramAnimation { animation { file = File("clip.mp4") }; caption = "..." }` accept the same file sources as the other send builders. Telegram also restricts which sticker and animation formats are accepted.

Use `editTelegramMessageCaption` to change or clear a caption, and `editTelegramMedia` to replace the media and optional caption. For example:

```kotlin
editTelegramMedia {
    messageId = update.callbackQuery.message.messageId
    media {
        type = TelegramMediaType.PHOTO
        file = File("replacement.jpg")
        caption = "Новое фото"
    }
}
```

The edit methods derive the chat ID from the incoming update, unless an explicit `chatId` is supplied. For inline-mode messages, use `inlineMessageId` and a Telegram `file_id` or URL: uploading a new file is not supported by Telegram in this case. Telegram applies further restrictions to edits of media groups and already-sent messages. These methods do not download files or automatically retry failed edits.
