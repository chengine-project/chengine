---
layout: default
lang: zh
title: 完整示例
section: 示例
permalink: /zh/examples/
lead: 可直接在文档中查看和复制的完整处理器。
---

所有示例都适用于使用 Telegram starter 的 Spring Boot 应用。请按照[快速开始]({{ '/zh/start/' | relative_url }})创建项目，然后将**一个**处理器添加到应用的包中。有些示例使用相同的命令或通用事件处理器，因此未经调整不要同时启用所有示例。

- [目录](#catalog) — 按钮、跳转和消息编辑。
- [对话](#conversation) — 使用场景图收集姓名和城市。
- [按钮](#buttons) — 回调和链接。
- [编辑](#editing) — 修改文本和键盘。
- [媒体](#media) — 发送文件。
- [投票](#polls) — 投票并在原始聊天中回复。
- [多条消息](#multiple) — 按顺序发送回复。
- [键盘和视频](#more-dsl) — 回复键盘和视频消息。
- [PostgreSQL](#postgres) — 持久化会话。
- [智能体工具](#agent) — 向当前聊天发送消息。

## 目录 {#catalog}

命令 `/catalog` 打开可翻页的商品目录和详情卡片。每次操作都会更新同一条消息。

<details markdown="1">
<summary>显示 CatalogHandler.kt</summary>
{% highlight kotlin %}{% include examples/zh/CatalogHandler.kt %}{% endhighlight %}
</details>

## 对话 {#conversation}

命令 `/start` 开始询问姓名和城市；输入 `跳过` 可跳过城市，`/cancel` 结束对话。默认情况下，数据保存在内存中。

<details markdown="1">
<summary>显示 ConversationHandler.kt</summary>
{% highlight kotlin %}{% include examples/zh/ConversationHandler.kt %}{% endhighlight %}
</details>

## 内联按钮 {#buttons}

命令 `/start` 发送一个回调按钮和文档链接。

<details markdown="1">
<summary>显示 InlineKeyboardHandler.kt</summary>
{% highlight kotlin %}{% include examples/zh/InlineKeyboardHandler.kt %}{% endhighlight %}
</details>

## 编辑消息 {#editing}

发送 `/edit` 并点击按钮，先修改文本，再修改同一条消息的键盘。

<details markdown="1">
<summary>显示 EditMessageHandler.kt</summary>
{% highlight kotlin %}{% include examples/zh/EditMessageHandler.kt %}{% endhighlight %}
</details>

## 照片、文档、视频和音频 {#media}

将自己的 `photo.jpg`、`document.pdf`、`video.mp4` 和 `audio.mp3` 放入应用工作目录，然后使用 `/photo`、`/document`、`/video` 和 `/audio` 命令。

<details markdown="1">
<summary>显示 MediaHandler.kt</summary>
{% highlight kotlin %}{% include examples/zh/MediaHandler.kt %}{% endhighlight %}
</details>

## 投票和测验 {#polls}

命令 `/poll` 和 `/quiz` 创建非匿名投票。只要投票关联信息可用，机器人就可以在投票后向原始聊天回复。

<details markdown="1">
<summary>显示 PollHandler.kt</summary>
{% highlight kotlin %}{% include examples/zh/PollHandler.kt %}{% endhighlight %}
</details>

有关存储配置的详细信息，请参阅[会话和 PostgreSQL]({{ '/zh/sessions/' | relative_url }})以及[投票]({{ '/zh/polls/' | relative_url }})。[所有原始示例](https://github.com/chengine-project/chengine/tree/develop/examples)也可在仓库中找到。

## 多条消息 {#multiple}

处理器可以返回回复列表；消息将按顺序依次发送：

```kotlin
@HandleCommand("/hello")
fun hello() = listOf(
    sendTelegramMessage { text = "第一条消息" },
    sendTelegramMessage { text = "第二条消息" }
)
```

## 键盘和视频消息 {#more-dsl}

```kotlin
sendTelegramVideoNote { videoNote { file = File("note.mp4") } }
sendTelegramMessage {
    text = "请选择答案"
    replyKeyboard { row { button { text = "是" }; button { text = "否" } } }
}
sendTelegramMessage { text = "完成"; replyKeyboard { removeKeyboard() } }
```

视频消息需要合适的正方形视频文件。同一条消息不能同时附加回复键盘和内联键盘。

## 在 PostgreSQL 中存储会话 {#postgres}

添加 `session-postgres` 模块，配置 `DataSource` 并注册 bean：

```kotlin
@Bean
fun sessionStore(dataSource: DataSource): SessionStore =
    PostgresSessionStore(dataSource).also { it.initializeSchema() }
```

导入：`org.springframework.context.annotation.Bean`、`javax.sql.DataSource`、`io.chengine.session.SessionStore`、`io.chengine.session.postgres.PostgresSessionStore`。更多信息请参阅[会话章节]({{ '/zh/sessions/' | relative_url }})。

## 代理工具 {#agent}

实验性工具会将文本发送到当前请求的聊天，并返回消息 ID。`request` 和 `client` 由应用提供：

```kotlin
fun sendToCurrentChat(request: BotRequestContext, client: TelegramClient): Int =
    TelegramSendMessageTool(request, client).execute("你好！")
```

导入：`io.chengine.connector.BotRequestContext`、`org.telegram.telegrambots.meta.generics.TelegramClient`、`io.chengine.agent.TelegramSendMessageTool`。此示例不包含真实模型或自动工具调用循环。更多信息请参阅[图章节]({{ '/zh/graphs/' | relative_url }})。
