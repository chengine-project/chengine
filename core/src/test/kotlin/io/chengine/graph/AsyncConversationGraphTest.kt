package io.chengine.graph

import io.chengine.session.ConversationSession
import io.chengine.session.SessionKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AsyncConversationGraphTest {
    private fun session() = ConversationSession(SessionKey("1", "2", "test", "bot"), "old")

    @Test fun `waits resumes and preserves transitions`() = runBlocking {
        val graph = AsyncConversationGraph("ask", mapOf(
            "ask" to { _, _ -> GraphTransition.Await("answer", "Question") },
            "answer" to { state, input -> state["name"] = requireNotNull(input); GraphTransition.Next("finish") },
            "finish" to { state, input -> assertNull(input); GraphTransition.Finish(state["name"]!!) },
        ))
        val state = session()
        assertEquals(GraphResult("Question", false), graph.start(state))
        assertEquals("answer", state.step)
        assertEquals(GraphResult("Hello", true), graph.resume(state, "Hello"))
    }

    @Test fun `suspended node releases the thread and cancellation propagates`() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val gate = CompletableDeferred<Unit>()
        val graph = AsyncConversationGraph("wait", mapOf(
            "wait" to { _, _ -> entered.complete(Unit); gate.await(); GraphTransition.Finish("done") },
        ))
        val state = session()
        val task = launch(start = CoroutineStart.UNDISPATCHED) { graph.start(state) }
        // This coroutine runs on the same runBlocking thread while the node is waiting.
        withTimeout(1_000) { entered.await() }
        assertFalse(task.isCompleted)
        task.cancelAndJoin()
        assertTrue(task.isCancelled)
        assertEquals("wait", state.step)
    }

    @Test fun `other sessions progress while one node is suspended`() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val gate = CompletableDeferred<Unit>()
        val graph = AsyncConversationGraph("start", mapOf(
            "start" to { state, _ ->
                if (state.sessionKey().userId == "slow") { entered.complete(Unit); gate.await() }
                GraphTransition.Finish(state.sessionKey().userId!!)
            },
        ))
        val slow = ConversationSession(SessionKey("slow", "chat", "test"), "old")
        val fast = ConversationSession(SessionKey("fast", "chat", "test"), "old")
        val pending = async(start = CoroutineStart.UNDISPATCHED) { graph.start(slow) }
        withTimeout(1_000) { entered.await() }
        assertEquals("fast", withTimeout(1_000) { graph.start(fast) }.message)
        gate.complete(Unit)
        assertEquals("slow", pending.await().message)
    }

    @Test fun `rejects missing nodes and limits cycles`() = runBlocking {
        assertThrows(IllegalArgumentException::class.java) { AsyncConversationGraph("missing", emptyMap()) }
        val state = session()
        val graph = AsyncConversationGraph("start", mapOf("start" to { _, _ -> GraphTransition.Next("missing") }))
        try { graph.start(state); fail("Expected unknown node") } catch (_: IllegalStateException) { }
        assertEquals("start", state.step)
        val loop = AsyncConversationGraph("loop", mapOf("loop" to { _, _ -> GraphTransition.Next("loop") }), 3)
        try { loop.start(session()); fail("Expected transition limit") } catch (_: IllegalStateException) { }
    }
}
