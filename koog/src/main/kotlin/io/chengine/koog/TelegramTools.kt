package io.chengine.koog

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import io.chengine.agent.TelegramSendMessageTool
import java.util.concurrent.atomic.AtomicInteger

/** A request-bound Koog tool set. Create a fresh instance for each Telegram chat. */
class TelegramTools(private val sender: TelegramSendMessageTool) : ToolSet {
    private val deliveries = AtomicInteger()

    /** Successful sends performed by this tool set, useful to avoid echoing a final answer twice. */
    val sentCount: Int get() = deliveries.get()

    @Tool("send_telegram_message")
    @LLMDescription("Send a plain-text message to the current Telegram chat and return its message ID")
    fun sendTelegramMessage(@LLMDescription("Text to send") text: String): Int =
        sender.execute(text).also { deliveries.incrementAndGet() }
}
