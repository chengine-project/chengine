package io.chengine.koog

import ai.koog.agents.core.agent.AIAgent
import io.chengine.graph.AsyncGraphNode
import io.chengine.graph.GraphTransition
import kotlinx.coroutines.withTimeout

/** A suspending terminal node. The caller manages the agent's lifecycle and request isolation. */
fun koogAgentNode(agent: AIAgent<String, String>, timeoutMillis: Long = 30_000): AsyncGraphNode {
    require(timeoutMillis > 0) { "timeoutMillis must be positive" }
    return { _, input ->
        val result = withTimeout(timeoutMillis) { agent.run(requireNotNull(input) { "Agent node requires input" }) }
        GraphTransition.Finish(result)
    }
}
