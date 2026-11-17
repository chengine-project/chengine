---
layout: default
lang: zh
title: 图与实验功能
section: 功能
permalink: /zh/graphs/
lead: 使用节点、转换和等待下一条消息来构建对话。
---

`ConversationGraph` 是构建在 `ConversationSession` 之上的实验性同步图。节点接收状态和传入文本，然后返回以下转换之一：

- `Next("node")` — 立即执行下一个节点；
- `Await("node", "消息")` — 返回消息并等待指定节点的下一条文本；
- `Finish("消息")` — 结束流程。

```kotlin
val graph = ConversationGraph("start", mapOf(
    "start" to { _, _ -> GraphTransition.Await("name", "你叫什么名字？") },
    "name" to { session, input ->
        session["name"] = input.orEmpty()
        GraphTransition.Next("finish")
    },
    "finish" to { session, _ -> GraphTransition.Finish("你好，${session["name"]}！") }
))
```

在处理器中使用 `Conversations.start` 创建会话，调用 `graph.start(session)` 并返回结果文本。收到下一条消息时调用 `graph.resume(session, text)`。如果 `result.completed` 为 `true`，请调用 `Conversations.end()` 结束会话。自动节点之间通过会话传递数据：`Next` 不会转发输入文本。转换次数受到限制，以防止无限循环。

[包含姓名、城市和条件分支的完整示例](https://github.com/chengine-project/chengine/tree/develop/examples/conversation)。

## 代理工具

实验性的 `TelegramSendMessageTool` 绑定当前请求的聊天，并通过传入的 Telegram 客户端发送文本。模型提供文本，聊天取自上下文。项目包含使用假模型的测试；目前还没有现成的 LLM 适配器或代理循环。[说明与限制](https://github.com/chengine-project/chengine/tree/develop/examples/agent-tool)。
