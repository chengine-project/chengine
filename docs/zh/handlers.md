---
layout: default
lang: zh
title: 命令和处理器
section: 基础
permalink: /zh/handlers/
lead: 声明由命令、文本或 Telegram 事件类型触发的方法。
---

## 命令

使用 `@Handler` 标记 Spring 组件，并用 `@HandleCommand` 标记方法。返回 Telegram 方法，例如 `sendTelegramMessage` 的结果。

```kotlin
@Handler
@Component
class MenuHandler {
    @HandleCommand("/help")
    fun help() = sendTelegramMessage {
        text = "有什么可以帮你？"
    }
}
```

群聊中支持 `/help@bot_username`。空格后的参数不属于命令路径；完整文本仍保留在 `Update` 中。

```kotlin
@HandleCommand("/start")
fun start(update: Update) = sendTelegramMessage {
    text = "收到：${update.message.text}"
}
```

导入 `org.telegram.telegrambots.meta.api.objects.Update`。未知或格式错误的命令会被忽略；注册错误和重复路径会在启动时检测到。

## 文本精确匹配

```kotlin
@HandleText("帮助")
fun helpText() = sendTelegramMessage { text = "帮助信息" }
```

该注解位于 `io.chengine.text.HandleText`。对于其他文本，可使用 `io.chengine.handler` 中的 `@TelegramHandleText`。

## 多个回复

处理器可以返回一个 Telegram 方法列表：

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "第一条消息" },
    sendTelegramMessage { text = "第二条消息" }
)
```

回复按照列表顺序依次发送。如果某次发送失败，后续方法不会执行；已发送的消息不会回滚。空列表不会发送任何内容。

## 处理器优先级

先查找命令，再查找精确匹配的文本，最后查找该事件类型的通用处理器。Chengine 命令路径还支持 `/item/id#42` 格式，配合 `@CommandParameter("id")` 使用。

[对话示例 →]({{ '/zh/sessions/' | relative_url }})
