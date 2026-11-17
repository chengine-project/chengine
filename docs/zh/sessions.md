---
layout: default
lang: zh
title: 会话和对话
section: 功能
permalink: /zh/sessions/
lead: 在消息之间保存步骤和用户数据。
---

## 开始对话

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.session.Conversations

@HandleCommand("/start")
fun start(request: BotRequestContext) = sendTelegramMessage {
    Conversations.start(request, "name")
    text = "你叫什么名字？"
}
```

创建会话时，传入事件必须包含用户和聊天。调用 `start` 前可以检查 `request.sessionKey()` 是否可用。

## 处理回复

```kotlin
@TelegramHandleText
fun answer(request: BotRequestContext) = sendTelegramMessage {
    val session = Conversations.current()
    val answer = request.textContent()?.trim().orEmpty()
    text = when {
        session == null -> "请先发送 /start"
        session.step == "name" -> {
            session["name"] = answer
            session.step = "city"
            "你在哪个城市？"
        }
        session.step == "city" -> {
            val name = session["name"]
            Conversations.end()
            "很高兴认识你，$name！你所在的城市是：$answer。"
        }
        else -> "请先发送 /start"
    }
}
```

要取消对话，可在 `/cancel` 处理器中调用 `Conversations.end()`。这是直接操作会话的示例；[完整示例]({{ '/zh/examples/#conversation' | relative_url }})使用图，并检查空回复与缺失的会话键。

## 存储和限制

默认情况下，会话保存在内存中，重启后会丢失。默认不活动超时时间为 10 分钟；调用 `Conversations.start` 时可通过 `timeout` 和 `unit` 参数修改。

会话键包含平台、机器人账号、用户和聊天。Telegram 使用配置的机器人用户名，因此更改用户名会改变键。同一会话的请求在单个 JVM 内串行处理；不保证同时到达的消息顺序。

## PostgreSQL

`session-postgres` 模块可在重启后保留 `ConversationSession`。添加依赖 `io.github.chengine-project:session-postgres:1.0.1-BETA`，配置 PostgreSQL `DataSource` 并注册 bean：

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

需要导入 `io.chengine.session.SessionStore`、`io.chengine.session.postgres.PostgresSessionStore`、`javax.sql.DataSource` 和 `org.springframework.context.annotation.Bean`。生产应用也可以在数据库架构配置中单独建表。使用 `deleteExpired()` 删除过期记录。此存储支持 `ConversationSession`；可实现 `SessionStore` 接口来使用其他存储。

PostgreSQL 会保存状态，但不会同步多个进程；多进程部署需要额外的协调机制。保存状态和发送消息不属于同一个事务。

## 场景图

实验性的 `ConversationGraph` 在同一会话之上提供命名节点、条件转换和等待下一个回复。[了解图的工作方式 →]({{ '/zh/graphs/' | relative_url }})

[完整示例]({{ '/zh/examples/#conversation' | relative_url }})
