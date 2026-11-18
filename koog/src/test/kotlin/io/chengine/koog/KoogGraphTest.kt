package io.chengine.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.testing.tools.getMockExecutor
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import io.chengine.graph.ConversationGraph
import io.chengine.graph.GraphTransition
import io.chengine.session.ConversationSession
import io.chengine.session.SessionKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KoogGraphTest {
    @Test fun `graph resumes user input through a mocked Koog agent`() {
        val agent = AIAgent(
            promptExecutor = getMockExecutor { mockLLMAnswer("Привет, Аня!").asDefaultResponse },
            llmModel = OpenAIModels.Chat.GPT4oMini,
            systemPrompt = "Greet the user by name.",
            maxIterations = 3,
        )
        val graph = ConversationGraph("ask", mapOf(
            "ask" to { _, _ -> GraphTransition.Await("greet", "Как тебя зовут?") },
            // This intentionally blocks: an isolated experiment for the current synchronous graph API.
            "greet" to { _, input ->
                val name = requireNotNull(input)
                val reply = runBlocking { withTimeout(10_000) { agent.run(name) } }
                GraphTransition.Finish(reply)
            },
        ))
        val session = ConversationSession(SessionKey("1", "123", "telegram", "bot"), "ask")
        val first = graph.start(session)
        assertEquals("Как тебя зовут?", first.message)
        assertFalse(first.completed)
        val result = graph.resume(session, "Аня")
        assertEquals("Привет, Аня!", result.message)
        assertTrue(result.completed)
    }
}
