package io.chengine.graph

import io.chengine.session.ConversationSession

/** A synchronous graph that pauses between incoming messages. */
class ConversationGraph(
    private val entry: String,
    nodes: Map<String, (ConversationSession, String?) -> GraphTransition>,
    private val maxTransitions: Int = 32
) {
    private val nodes = nodes.toMap()

    init {
        require(entry in nodes) { "Entry node '$entry' does not exist" }
        require(maxTransitions > 0) { "maxTransitions must be positive" }
    }

    fun start(session: ConversationSession): GraphResult {
        session.step = entry
        return run(session, null)
    }

    fun resume(session: ConversationSession, input: String): GraphResult = run(session, input)

    private fun run(session: ConversationSession, input: String?): GraphResult {
        var nextInput = input
        repeat(maxTransitions) {
            val node = checkNotNull(nodes[session.step]) { "Unknown graph node '${session.step}'" }
            when (val transition = node(session, nextInput)) {
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

sealed interface GraphTransition {
    data class Next(val node: String) : GraphTransition
    data class Await(val node: String, val message: String) : GraphTransition
    data class Finish(val message: String) : GraphTransition
}

data class GraphResult(val message: String, val completed: Boolean)
