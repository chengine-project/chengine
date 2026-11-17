package io.chengine.autoconfigure

import io.chengine.Chengine
import io.chengine.ChengineConfig
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.TelegramBotsLongPoolingBot
import io.chengine.command.HandleCommand
import io.chengine.dsl.TelegramPollType
import io.chengine.dsl.sendTelegramPoll
import io.chengine.handler.Handler
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentCaptor
import org.mockito.Mockito.*
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient

@Handler
class PollTransportHandler {
    @HandleCommand("/poll") fun poll() = sendTelegramPoll { question = "Choose"; options = listOf("A", "B"); isAnonymous = false }
    @HandleCommand("/quiz") fun quiz() = sendTelegramPoll { question = "2+2"; options = listOf("3", "4"); type = TelegramPollType.QUIZ; correctOptionIds = listOf(1) }
}
class PollTransportTest {
    @Test fun `regular poll and quiz reach Telegram client with chat id`() {
        val client = mock(TelegramClient::class.java); val bot = ChengineTelegramLongPoolingBot()
        val adapter = TelegramBotsLongPoolingBot("test-token", "test_bot", bot, client); bot.telegramBotsLongPollingBotBot = adapter
        Chengine(ChengineConfig(listOf(PollTransportHandler()), listOf(bot)))
        fun send(command: String) = adapter.consume(listOf(Update().apply { message = Message().apply { text = command; chat = Chat(42L, "private") } }))
        send("/poll"); send("/quiz")
        val polls = ArgumentCaptor.forClass(SendPoll::class.java); verify(client, times(2)).execute(polls.capture())
        assertEquals(listOf("42", "42"), polls.allValues.map { it.chatId })
        assertEquals(listOf("regular", "quiz"), polls.allValues.map { it.type })
        assertEquals(listOf(1), polls.allValues.last().correctOptionIds)
    }
}
