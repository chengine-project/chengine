package io.chengine.graph

import io.chengine.session.ConversationSession
import io.chengine.session.SessionKey
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ConversationGraphTest {
    private fun session() = ConversationSession(SessionKey("1", "2", "test"), "old")

    @Test fun `pauses resumes and passes state through automatic nodes`() {
        val graph = ConversationGraph("ask", mapOf(
            "ask" to { _, _ -> GraphTransition.Await("answer", "Question") },
            "answer" to { session, input -> session["answer"] = input!!; GraphTransition.Next("finish") },
            "finish" to { session, input ->
                assertNull(input)
                GraphTransition.Finish(session["answer"]!!)
            }
        ))
        val session = session()
        assertEquals(GraphResult("Question", false), graph.start(session))
        assertEquals("answer", session.step)
        assertEquals(GraphResult("Hello", true), graph.resume(session, "Hello"))
    }

    @Test fun `rejects missing nodes without moving session to an invalid node`() {
        assertThrows(IllegalArgumentException::class.java) { ConversationGraph("missing", emptyMap()) }
        val graph = ConversationGraph("start", mapOf("start" to { _, _ -> GraphTransition.Await("missing", "Hi") }))
        val session = session()
        assertThrows(IllegalStateException::class.java) { graph.start(session) }
        assertEquals("start", session.step)
    }

    @Test fun `limits cycles`() {
        val graph = ConversationGraph("loop", mapOf("loop" to { _, _ -> GraphTransition.Next("loop") }), 3)
        assertThrows(IllegalStateException::class.java) { graph.start(session()) }
    }
}
