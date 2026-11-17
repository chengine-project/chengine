---
layout: default
lang: zh
title: 按钮和回调
section: 功能
permalink: /zh/buttons/
lead: 为消息添加内联键盘并处理点击。
---

## 发送按钮

```kotlin
@HandleCommand("/start")
fun start() = sendTelegramMessage {
    text = "请点击按钮"
    inlineKeyboard {
        row {
            button {
                text = "测试"
                callbackData = "check_button"
            }
        }
    }
}
```

## 响应点击

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = AnswerCallbackQuery(update.callbackQuery.id).apply {
    text = "按钮正常工作！"
    showAlert = true
}
```

导入 `io.chengine.handler.TelegramHandleCallback`、`org.telegram.telegrambots.meta.api.objects.Update` 和 `org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery`。响应回调可清除 Telegram 客户端的等待指示。

如果 `callbackData` 与已注册的命令路径匹配，会优先调用命令处理器。其余回调事件由通用的 `@TelegramHandleCallback` 处理。

## 链接和多行按钮

```kotlin
inlineKeyboard {
    row {
        button { text = "返回"; callbackData = "prev" }
        button { text = "前进"; callbackData = "next" }
    }
    row { button { text = "文档"; url = "https://chengine-project.github.io/chengine/" } }
}
```

每个按钮必须恰好指定一个动作。回调数据长度为 1–64 个 UTF-8 字节。[目录示例]({{ '/zh/examples/#catalog' | relative_url }})演示如何在同一条消息中切换页面并查看商品详情。

## 编辑消息和键盘

`editTelegramMessageText` 更新文本，并可按需替换键盘；`editTelegramMessageReplyMarkup` 只更改键盘，其 `removeKeyboard()` 可移除键盘。请指定消息的 `messageId`；`chatId` 可以显式设置，也可以从传入事件中获取。内联消息使用 `inlineMessageId`，不使用前两个字段。编辑前请响应回调。

```kotlin
@TelegramHandleCallback
fun callback(update: Update) = listOf(
    AnswerCallbackQuery(update.callbackQuery.id),
    editTelegramMessageText {
        messageId = update.callbackQuery.message.messageId
        text = "完成"
        inlineKeyboard { row { button { text = "网站"; url = "https://chengine-project.github.io/chengine/" } } }
    }
)
```

## 回复键盘

```kotlin
sendTelegramMessage {
    text = "请选择答案"
    replyKeyboard { row { button { text = "是" }; button { text = "否" } } }
}
```

要移除回复键盘，请发送包含 `replyKeyboard { removeKeyboard() }` 的消息。一条消息只能包含回复键盘或内联键盘中的一种。

[完整示例]({{ '/zh/examples/#buttons' | relative_url }})
