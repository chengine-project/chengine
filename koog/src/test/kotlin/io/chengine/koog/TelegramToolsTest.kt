package io.chengine.koog

import ai.koog.agents.core.agent.AIAgent
import ai.koog.agents.core.tools.ToolRegistry
import ai.koog.agents.core.tools.reflect.ToolFromCallable
import ai.koog.agents.testing.tools.getMockExecutor
import ai.koog.prompt.executor.clients.openai.OpenAIModels
import ai.koog.prompt.executor.clients.openrouter.OpenRouterLLMClient
import ai.koog.prompt.executor.clients.openrouter.OpenRouterModels
import ai.koog.prompt.executor.llms.MultiLLMPromptExecutor
import ai.koog.prompt.llm.LLMProvider
import io.chengine.agent.TelegramSendMessageTool
import io.chengine.connector.DefaultBotRequestContext
import io.chengine.connector.TelegramBotApiIdentifier
import io.chengine.session.SessionKey
import io.chengine.session.ConversationSession
import io.chengine.graph.AsyncConversationGraph
import io.chengine.graph.GraphTransition
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.generics.TelegramClient
import java.lang.reflect.Proxy
import java.util.concurrent.Executors
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.valueParameters

class TelegramToolsTest {
    private fun sender(chatId: String, messages: MutableList<SendMessage>, fail: Boolean = false, beforeSend: (() -> Unit)? = null): TelegramSendMessageTool {
        val request = DefaultBotRequestContext().apply {
            botApiIdentifier = TelegramBotApiIdentifier.instance
            sessionKey = SessionKey("user", chatId, "telegram", "bot")
        }
        val client = Proxy.newProxyInstance(TelegramClient::class.java.classLoader, arrayOf(TelegramClient::class.java)) { _, method, args ->
            if (method.name == "execute" && args?.singleOrNull() is SendMessage) {
                if (fail) throw IllegalStateException("send failed")
                beforeSend?.invoke()
                messages.add(args[0] as SendMessage)
                Message().apply { messageId = 42 }
            } else error("Unexpected client method: ${method.name}")
        } as TelegramClient
        return TelegramSendMessageTool(request, client)
    }

    @Test fun `tool binds the chat and returns the sent message id`() {
        val sent = mutableListOf<SendMessage>()
        val first = TelegramTools(sender("100", sent))
        val second = TelegramTools(sender("200", sent))
        assertEquals(42, first.sendTelegramMessage("first"))
        assertEquals(42, second.sendTelegramMessage("second"))
        assertEquals(1, first.sentCount)
        assertEquals(1, second.sentCount)
        assertEquals(listOf("100", "200"), sent.map { it.chatId })
        assertThrows(IllegalArgumentException::class.java) { first.sendTelegramMessage(" ") }
        assertThrows(IllegalStateException::class.java) { TelegramTools(sender("100", sent, fail = true)).sendTelegramMessage("test") }
        assertEquals(2, sent.size)
    }

    @Test fun `optional message limit prevents repeated successful sends`() {
        val sent = mutableListOf<SendMessage>()
        val tools = TelegramTools(sender("123", sent), maxMessages = 1)
        assertEquals(42, tools.sendTelegramMessage("first"))
        assertThrows(IllegalStateException::class.java) { tools.sendTelegramMessage("again") }
        assertEquals(1, tools.sentCount)
        assertEquals(1, sent.size)
        assertThrows(IllegalArgumentException::class.java) { TelegramTools(sender("123", sent), maxMessages = 0) }
    }

    @Test fun `message limit is enforced for concurrent tool calls`() {
        val sent = mutableListOf<SendMessage>()
        val tools = TelegramTools(sender("123", sent), maxMessages = 1)
        val start = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)
        try {
            val results = (1..2).map { number -> executor.submit<Boolean> {
                start.await()
                runCatching { tools.sendTelegramMessage("message $number") }.isSuccess
            } }
            start.countDown()
            assertEquals(1, results.count { it.get() })
            assertEquals(1, sent.size)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test fun `blocking Telegram client runs away from the calling coroutine thread`() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val release = CountDownLatch(1)
        val messages = mutableListOf<SendMessage>()
        val tools = TelegramTools(sender("123", messages, beforeSend = {
            entered.complete(Unit)
            check(release.await(5, TimeUnit.SECONDS))
        }))
        try {
            val pending = async(start = CoroutineStart.UNDISPATCHED) { tools.sendToCurrentChat("hello") }
            withTimeout(2_000) { entered.await() }
            org.junit.jupiter.api.Assertions.assertFalse(pending.isCompleted)
            release.countDown()
            assertEquals(42, pending.await())
        } finally {
            release.countDown()
        }
    }

    @Test fun `mock model requests the Koog tool and sends only to bound chat`() = runBlocking {
        val sent = mutableListOf<SendMessage>()
        val tool = TelegramTools(sender("123", sent)).getTool("send_telegram_message")
        assertEquals(listOf("text"), tool.descriptor.requiredParameters.map { it.name })
        assertEquals(emptyList<String>(), tool.descriptor.optionalParameters.map { it.name })
        val args = ToolFromCallable.Args(mapOf(tool.callable.valueParameters.single() to "Hello from Koog"))
        val executor = getMockExecutor {
            mockLLMToolCall(tool, args) onRequestEquals "Send greeting"
            mockLLMAnswer("Done").asDefaultResponse
        }
        val registry = ToolRegistry { tool(tool) }
        val agent = AIAgent(
            promptExecutor = executor,
            llmModel = OpenAIModels.Chat.GPT4oMini,
            toolRegistry = registry,
            systemPrompt = "Send the requested message using the available tool.",
            maxIterations = 5,
        )
        agent.run("Send greeting")
        assertEquals(1, sent.size)
        assertEquals("123", sent.single().chatId)
        assertEquals("Hello from Koog", sent.single().text)
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CHENGINE_TEST_OPENROUTER", matches = "true")
    fun `OpenRouter model invokes the request bound Telegram tool`() = runBlocking {
        val key = requireNotNull(System.getenv("OPENROUTER_API_KEY"))
        val sent = mutableListOf<SendMessage>()
        val tools = TelegramTools(sender("123", sent))
        val agent = AIAgent(
            promptExecutor = MultiLLMPromptExecutor(LLMProvider.OpenRouter to OpenRouterLLMClient(key)),
            llmModel = OpenRouterModels.GPT4oMini,
            toolRegistry = ToolRegistry { tools(tools) },
            systemPrompt = "You must use the send_telegram_message tool to send the requested message. Never answer without calling this tool.",
            maxIterations = 12,
        )
        val graph = AsyncConversationGraph("wait", mapOf(
            "wait" to { _, _ -> GraphTransition.Await("agent", "") },
            "agent" to koogAgentNode(agent, timeoutMillis = 60_000),
        ))
        val session = ConversationSession(SessionKey("user", "123", "telegram", "bot"), "wait")
        graph.start(session)
        graph.resume(session, "Send the exact text: Hello from Koog")
        assertEquals(1, tools.sentCount)
        assertEquals(1, sent.size)
        assertEquals("123", sent.single().chatId)
        assertEquals("Hello from Koog", sent.single().text)
    }
}
