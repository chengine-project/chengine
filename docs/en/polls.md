---
layout: default
lang: en
title: Polls and quizzes
section: FEATURES
permalink: /en/polls/
lead: Create polls and handle both individual answers and aggregate state changes.
---

## Regular poll

```kotlin
import io.chengine.dsl.sendTelegramPoll

@HandleCommand("/poll")
fun poll() = sendTelegramPoll {
    question = "Cats or dogs?"
    options = listOf("Cats", "Dogs")
    isAnonymous = false
}
```

Telegram polls are anonymous by default. Set `isAnonymous = false` to receive individual answers.

## Quiz

```kotlin
import io.chengine.dsl.TelegramPollType

@HandleCommand("/quiz")
fun quiz() = sendTelegramPoll {
    question = "What is 2 + 2?"
    options = listOf("3", "4", "5")
    type = TelegramPollType.QUIZ
    correctOptionIds = listOf(1)
    explanation = "2 + 2 = 4"
    isAnonymous = false
}
```

Correct answer indexes are zero-based and specified in ascending order. The DSL validates the question, number and length of options, indexes, and some timing parameters.

## Voting events

Use `@TelegramHandlePollAnswer` for individual answers and `@TelegramHandlePoll` for poll state. Both handlers receive the original `Update`.

```kotlin
import io.chengine.connector.BotRequestContext
import io.chengine.telegramPollOrigin

@TelegramHandlePollAnswer
fun answer(update: Update, request: BotRequestContext) =
    if (request.telegramPollOrigin() != null) sendTelegramMessage {
        text = if (update.pollAnswer.optionIds.isEmpty()) "Vote withdrawn" else "Thanks for your answer!"
    } else Unit
```

`telegramPollOrigin()` returns the poll ID, original chat, and message ID when known. If the handler returns a message without an explicit `chatId`, Chengine sends it to the original chat. A user may retract a vote, leaving `optionIds` empty. `voterChat` may be present instead of `user`.

By default poll mappings are stored in a bounded in-memory store and are lost after a restart. For persistent storage, implement `TelegramPollOriginStore` and set `ChengineTelegramLongPoolingBot.pollOriginStore` before handling events. The mapping can be unavailable for polls sent outside Chengine, expired entries, or a race during sending. Poll updates do not create sessions; the original poll chat is not necessarily the voter's chat.

> Individual answers are delivered only for non-anonymous polls sent by the bot. Anonymous polls produce aggregate state updates.

[Complete example in the repository](https://github.com/chengine-project/chengine/tree/develop/examples/polls)
