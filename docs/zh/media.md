---
layout: default
lang: zh
title: 照片、文件和媒体
section: 功能
permalink: /zh/media/
lead: 发送和接收照片、文档、视频、音频及其他媒体。
---

## 磁盘文件

```kotlin
import io.chengine.dsl.sendTelegramPhoto
import java.io.File

@HandleCommand("/photo")
fun photo() = sendTelegramPhoto {
    photo { file = File("photo.jpg") }
    caption = "来自文件的照片"
}
```

如果没有显式设置 `chatId`，它会从传入消息中填充。`sendTelegramDocument`、`sendTelegramVideo` 和 `sendTelegramAudio` 的使用方式类似。

```kotlin
@HandleCommand("/document")
fun document() = sendTelegramDocument {
    document { file = File("document.pdf") }
    caption = "文档"
}
```

## File ID 和流

```kotlin
val message = sendTelegramVideo {
    video { name = "TELEGRAM_FILE_ID" }
    supportsStreaming = true
}

val audioMessage = sendTelegramAudio {
    audio {
        mediaStream = inputStream
        name = "audio.mp3"
    }
    title = "录音"
}
```

没有文件或流时，`name` 表示 Telegram `file_id` 或 URL。使用流时必须提供名称；已消费的流不能重复发送。也可以直接将 `InputFile` 赋给 `photo`、`document`、`video` 或 `audio` 属性。

还提供 `sendTelegramSticker`、`sendTelegramAnimation` 和 `sendTelegramVideoNote`。视频支持尺寸和时长，音频支持标题和表演者。Telegram 要求视频消息使用合适的正方形视频文件。

## 接收媒体

```kotlin
@TelegramHandleMedia
fun media(update: Update) = sendTelegramMessage {
    text = if (update.message.photo != null) "已收到照片" else "已收到文件"
}
```

原始 `Update` 中可获取照片、文档、视频、音频、语音、动画、视频消息或贴纸；说明文字位于 `update.message.caption`。要下载文件，请通过其 `fileId` 使用 Telegram API。

## 修改说明文字和文件

`editTelegramMessageCaption` 修改说明文字，`editTelegramMedia` 替换媒体：

```kotlin
editTelegramMedia {
    messageId = update.callbackQuery.message.messageId
    media {
        type = TelegramMediaType.PHOTO
        file = File("replacement.jpg")
        caption = "新照片"
    }
}
```

可以设置 `chatId`，也可以由 Chengine 从传入事件中获取。对于内联消息，请设置 `inlineMessageId`：此时 Telegram 只接受 `file_id` 或 URL，不接受新上传的文件。Telegram 对某些消息和媒体组的编辑还有其他限制。

[仓库中的完整示例](https://github.com/chengine-project/chengine/tree/develop/examples/media)
