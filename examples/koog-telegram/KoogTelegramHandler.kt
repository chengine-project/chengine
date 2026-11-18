package example.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.prompt.executor.clients.openrouter.OpenRouterModels
import ai.koog.prompt.executor.clients.openrouter.OpenRouterLLMClient
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import io.chengine.ChengineTelegramLongPoolingBot
import io.chengine.agent.TelegramSendMessageTool
import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.handler.Handler
import io.chengine.koog.TelegramTools
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Component

/** Minimal synchronous demonstration, not a production job queue. */
@Handler
@Component
class KoogTelegramHandler(private val bot: ChengineTelegramLongPoolingBot) {
    @HandleCommand("/koog")
    fun greeting(request: BotRequestContext): Unit {
        val key = requireNotNull(System.getenv("OPENROUTER_API_KEY")) { "Set OPENROUTER_API_KEY" }
        // Capture the chat now; do not read Chengine's thread-local session inside the coroutine.
        val sender = TelegramSendMessageTool(request, bot.telegramBotsLongPollingBotBot.client)
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
            val result = runBlocking { withTimeout(30_000) { agent.run("Say hello from Chengine and Koog.") } }
            // Send a final response only when the model did not already send through the tool.
            if (tools.sentCount == 0 && result.isNotBlank()) sender.execute(result)
        } finally {
            executor.close()
        }
    }
}
