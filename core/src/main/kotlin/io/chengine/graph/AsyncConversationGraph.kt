package io.chengine.graph

import io.chengine.session.ConversationSession
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/** A graph node which may suspend. It receives its session explicitly, not through thread-local context. */
typealias AsyncGraphNode = suspend (ConversationSession, String?) -> GraphTransition

/**
 * A coroutine-based graph that pauses between incoming messages and can suspend inside a node.
 * The caller owns the coroutine, session loading and saving, and serialization of requests for the
 * same session. Do not access Chengine's thread-local session context from suspending nodes.
 */
class AsyncConversationGraph(
    private val entry: String,
    nodes: Map<String, AsyncGraphNode>,
    private val maxTransitions: Int = 32,
) {
    private val nodes = nodes.toMap()

    init {
        require(entry in nodes) { "Entry node '$entry' does not exist" }
        require(maxTransitions > 0) { "maxTransitions must be positive" }
    }

    suspend fun start(session: ConversationSession): GraphResult {
        coroutineContext.ensureActive()
        session.step = entry
        return run(session, null)
    }

    suspend fun resume(session: ConversationSession, input: String): GraphResult = run(session, input)

    private suspend fun run(session: ConversationSession, input: String?): GraphResult {
        var nextInput = input
        repeat(maxTransitions) {
            coroutineContext.ensureActive()
            val node = checkNotNull(nodes[session.step]) { "Unknown graph node '${session.step}'" }
            val transition = node(session, nextInput)
            // A node may return after cancellation without suspending; do not commit its transition.
            coroutineContext.ensureActive()
            when (transition) {
                is GraphTransition.Next -> {
                    requireNode(transition.node)
                    session.step = transition.node
                    nextInput = null
                }
                is GraphTransition.Await -> {
                    requireNode(transition.node)
                    session.step = transition.node
                    return GraphResult(transition.message, completed = false)
                }
                is GraphTransition.Finish -> return GraphResult(transition.message, completed = true)
            }
        }
        error("Graph exceeded $maxTransitions transitions")
    }

    private fun requireNode(name: String) {
        check(name in nodes) { "Unknown graph node '$name'" }
    }
}
