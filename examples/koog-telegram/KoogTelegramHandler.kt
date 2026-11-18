package example.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.clients.openrouter.OpenRouterLLMClient
import ai.koog.prompt.executor.clients.openrouter.OpenRouterModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.agent.TelegramSendMessageTool
import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.graph.AsyncConversationGraph
import io.chengine.graph.GraphTransition
import io.chengine.handler.Handler
import io.chengine.koog.TelegramTools
import io.chengine.koog.koogAgentNode
import io.chengine.session.ConversationSession
import io.chengine.session.SessionKey
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.DisposableBean
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/** A lifecycle-bound example; active requests are serialized by rejecting a second run per session. */
@Handler
@Component
class KoogTelegramHandler(private val bot: ChengineTelegramLongPoolingBot) : DisposableBean {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val jobs = AsyncSessionJobs(scope)

    @HandleCommand("/koog")
    fun greeting(request: BotRequestContext) {
        val key = requireNotNull(System.getenv("OPENROUTER_API_KEY")) { "Set OPENROUTER_API_KEY" }
        // Only immutable identifiers and the request-bound sender cross the coroutine boundary.
        val sessionKey = requireNotNull(request.sessionKey()) { "A Telegram session key is required" }
        val sender = TelegramSendMessageTool(request, bot.telegramBotsLongPollingBotBot.client)
        jobs.submit(sessionKey) {
            try {
                runAgent(key, sessionKey, sender)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                logger.error("Koog request failed", error)
            }
        }
    }

    private suspend fun runAgent(key: String, sessionKey: SessionKey, sender: TelegramSendMessageTool) {
        val tools = TelegramTools(sender, maxMessages = 1)
        val executor = MultiLLMPromptExecutor(LLMProvider.OpenRouter to OpenRouterLLMClient(key))
        try {
            val agent = AIAgent(
                promptExecutor = executor,
                llmModel = OpenRouterModels.GPT4oMini,
                toolRegistry = ToolRegistry { tools(tools) },
                systemPrompt = "Greet the user briefly. Call send_telegram_message exactly once, then finish.",
                maxIterations = 12,
            )
            val graph = AsyncConversationGraph("wait", mapOf(
                "wait" to { _, _ -> GraphTransition.Await("agent", "") },
                "agent" to koogAgentNode(agent),
            ))
            // The demo uses a fresh graph session, independent of Chengine's thread-local session.
            val session = ConversationSession(sessionKey, "wait")
            graph.start(session)
            val result = graph.resume(session, "Say hello from Chengine and Koog.")
            if (tools.sentCount == 0 && result.message.isNotBlank()) {
                withContext(Dispatchers.IO) { sender.execute(result.message) }
            }
        } finally {
            executor.close()
        }
    }

    override fun destroy() {
        scope.cancel()
    }
}

/** Demo-only, in-process single-flight guard; does not coordinate multiple application instances. */
internal class AsyncSessionJobs(private val scope: CoroutineScope) {
    private val active = ConcurrentHashMap<SessionKey, Job>()

    fun submit(key: SessionKey, block: suspend () -> Unit): Boolean {
        val job = scope.launch(start = CoroutineStart.LAZY) { block() }
        job.invokeOnCompletion { active.remove(key, job) }
        if (active.putIfAbsent(key, job) != null) {
            job.cancel()
            return false
        }
        if (!job.start()) {
            active.remove(key, job)
            return false
        }
        return true
    }
}
