---
layout: default
lang: zh
title: 更轻松地创建机器人
section: 概览
permalink: /zh/
lead: Chengine 是一个用于 Telegram 机器人的 Kotlin 框架，提供熟悉的处理器模型与 Spring Boot 集成。
---

从第一条命令到多步骤对话：用 Kotlin 描述机器人的行为，Chengine 会找到处理器并将回复发送给 Telegram。

<a class="button" href="{{ '/zh/start/' | relative_url }}">开始使用 →</a><a class="button secondary" href="https://github.com/chengine-project/chengine">在 GitHub 上查看 ↗</a>

## 功能概览

<div class="cards">
  <a class="card" href="{{ '/zh/handlers/' | relative_url }}"><strong>命令和文本 →</strong><span>将传入事件绑定到处理方法。</span></a>
  <a class="card" href="{{ '/zh/buttons/' | relative_url }}"><strong>按钮和回调 →</strong><span>创建键盘并响应按钮点击。</span></a>
  <a class="card" href="{{ '/zh/media/' | relative_url }}"><strong>媒体和文件 →</strong><span>接收和发送照片、文件与视频。</span></a>
  <a class="card" href="{{ '/zh/sessions/' | relative_url }}"><strong>会话和对话 →</strong><span>在内存或 PostgreSQL 中保存状态。</span></a>
  <a class="card" href="{{ '/zh/polls/' | relative_url }}"><strong>投票 →</strong><span>发起投票并处理投票结果。</span></a>
  <a class="card" href="{{ '/zh/graphs/' | relative_url }}"><strong>图 →</strong><span>使用转换和等待步骤构建流程。</span></a>
</div>

## 快速示例

```kotlin
@Handler
@Component
class HelloHandler {
    @HandleCommand("/hello")
    fun hello() = sendTelegramMessage {
        text = "你好！"
    }
}
```

> 本文档描述 `develop` 分支。项目 API 仍在演进，部分功能处于实验阶段。
