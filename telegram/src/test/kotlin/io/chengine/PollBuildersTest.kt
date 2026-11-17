package io.chengine

import io.chengine.dsl.TelegramPollType
import io.chengine.dsl.ParseMode
import io.chengine.dsl.sendTelegramPoll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class PollBuildersTest {
    @Test fun `builds regular poll and quiz`() {
        val poll = sendTelegramPoll { question = "Choose"; option("One"); option("Two"); isAnonymous = false; allowMultipleAnswers = true }
        assertEquals("regular", poll.type); assertEquals(listOf("One", "Two"), poll.options.map { it.text })
        assertFalse(poll.isAnonymous); assertTrue(poll.allowMultipleAnswers)
        val quiz = sendTelegramPoll { question = "2+2"; options = listOf("3", "4"); type = TelegramPollType.QUIZ; correctOptionIds = listOf(1); explanation = "Four"; explanationParseMode = ParseMode.MARKDOWN_V2 }
        assertEquals("quiz", quiz.type); assertEquals(listOf(1), quiz.correctOptionIds); assertEquals("Four", quiz.explanation); assertEquals("MarkdownV2", quiz.explanationParseMode)
    }
    @Test fun `rejects invalid questions options and quiz indexes`() {
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = ""; option("A") } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q" } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q"; options = List(13) { "A" } } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q"; option(" ") } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q"; option("A"); type = TelegramPollType.QUIZ } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q"; option("A"); type = TelegramPollType.QUIZ; correctOptionIds = listOf(1) } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q"; option("A"); correctOptionIds = listOf(0) } }
        assertThrows(IllegalArgumentException::class.java) { sendTelegramPoll { question = "Q"; option("A"); openPeriod = 5; closeDate = 1 } }
    }
}
