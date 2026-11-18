package io.chengine.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.testing.tools.getMockExecutor
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.Prompt
import ai.koog.prompt.executor.model.PromptExecutor
import ai.koog.prompt.executor.model.PromptExecutorAPI
import ai.koog.prompt.executor.model.ResolvedModel
import ai.koog.prompt.llm.LLModel
import ai.koog.prompt.message.Message
import ai.koog.agents.core.tools.ToolDescriptor
import io.chengine.graph.AsyncConversationGraph
import io.chengine.graph.GraphTransition
import io.chengine.session.ConversationSession
import io.chengine.session.SessionKey
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KoogGraphTest {
    private class WaitingExecutor(
        private val delegate: PromptExecutor,
        private val entered: CompletableDeferred<Unit>,
        private val gate: CompletableDeferred<Unit>,
    ) : PromptExecutor(), PromptExecutorAPI by delegate {
        override suspend fun execute(prompt: Prompt, model: LLModel, tools: List<ToolDescriptor>): Message.Assistant {
            entered.complete(Unit)
            gate.await()
            return delegate.execute(prompt, model, tools)
        }

        override suspend fun execute(prompt: Prompt, model: ResolvedModel, tools: List<ToolDescriptor>): Message.Assistant =
            execute(prompt, model.effectiveModel, tools)

        override fun close() = delegate.close()
    }

    @Test fun `graph resumes user input through a mocked Koog agent`() = runBlocking {
        val agent = AIAgent(
            promptExecutor = getMockExecutor { mockLLMAnswer("Привет, Аня!").asDefaultResponse },
            llmModel = OpenAIModels.Chat.GPT4oMini,
            systemPrompt = "Greet the user by name.",
            maxIterations = 3,
        )
        val graph = AsyncConversationGraph("ask", mapOf(
            "ask" to { _, _ -> GraphTransition.Await("greet", "Как тебя зовут?") },
            "greet" to koogAgentNode(agent, timeoutMillis = 10_000),
        ))
        val session = ConversationSession(SessionKey("1", "123", "telegram", "bot"), "ask")
        val first = graph.start(session)
        assertEquals("Как тебя зовут?", first.message)
        assertFalse(first.completed)
        val result = graph.resume(session, "Аня")
        assertEquals("Привет, Аня!", result.message)
        assertTrue(result.completed)
    }

    @Test fun `Koog node suspends without blocking and responds to cancellation`() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val gate = CompletableDeferred<Unit>()
        val executor = WaitingExecutor(getMockExecutor { mockLLMAnswer("done").asDefaultResponse }, entered, gate)
        val agent = AIAgent(promptExecutor = executor, llmModel = OpenAIModels.Chat.GPT4oMini, systemPrompt = "Greet.")
        val graph = AsyncConversationGraph("agent", mapOf("agent" to koogAgentNode(agent, 10_000)))
        val session = ConversationSession(SessionKey("1", "123", "telegram", "bot"), "agent")
        val pending = async(start = CoroutineStart.UNDISPATCHED) { graph.resume(session, "hello") }
        withTimeout(1_000) { entered.await() }
        // Reaching here on the same thread proves the waiting Koog request has suspended.
        assertFalse(pending.isCompleted)
        pending.cancelAndJoin()
        assertTrue(pending.isCancelled)
        assertEquals("agent", session.step)
    }
}
