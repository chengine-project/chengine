---
layout: default
lang: zh
title: 投票和测验
section: 功能
permalink: /zh/polls/
lead: 创建投票并处理个人回复与汇总状态变化。
---

## 普通投票

```kotlin
import io.chengine.dsl.sendTelegramPoll

@HandleCommand("/poll")
fun poll() = sendTelegramPoll {
    question = "猫还是狗？"
    options = listOf("猫", "狗")
    isAnonymous = false
}
```

Telegram 默认创建匿名投票。要接收个人回复，请设置 `isAnonymous = false`。

## 测验

```kotlin
import io.chengine.dsl.TelegramPollType

@HandleCommand("/quiz")
fun quiz() = sendTelegramPoll {
    question = "2 + 2 等于多少？"
    options = listOf("3", "4", "5")
    type = TelegramPollType.QUIZ
    correctOptionIds = listOf(1)
    explanation = "2 + 2 = 4"
    isAnonymous = false
}
```

正确答案索引从零开始，按升序指定。DSL 会检查问题、选项数量和长度、索引以及部分时间参数。

## 投票事件

使用 `@TelegramHandlePollAnswer` 处理个人回复，使用 `@TelegramHandlePoll` 处理投票状态。两个处理器都接收原始 `Update`。

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.telegramPollOrigin

@TelegramHandlePollAnswer
fun answer(update: Update, request: BotRequestContext) =
    if (request.telegramPollOrigin() != null) sendTelegramMessage {
        text = if (update.pollAnswer.optionIds.isEmpty()) "投票已撤回" else "感谢你的回答！"
    } else Unit
```

如果关联信息可用，`telegramPollOrigin()` 返回投票 ID、原始聊天和消息 ID。如果处理器返回的消息未显式设置 `chatId`，Chengine 会将其发送到原始聊天。用户可以撤回投票，此时 `optionIds` 为空。也可能出现 `voterChat` 而不是 `user`。

默认情况下，投票关联信息保存在有限容量的内存存储中，重启后会丢失。要持久化，请实现 `TelegramPollOriginStore` 并在处理事件之前将其设置到 `ChengineTelegramLongPoolingBot.pollOriginStore`。对于由 Chengine 以外的方式发送的投票、已过期的记录或发送过程中的竞争，关联信息可能不可用。投票事件不会创建会话；投票的原始聊天不一定是投票者的聊天。

> 只有机器人发送的非匿名投票才会产生个人回复。匿名投票提供汇总状态事件。

[仓库中的完整示例](https://github.com/chengine-project/chengine/tree/develop/examples/polls)
