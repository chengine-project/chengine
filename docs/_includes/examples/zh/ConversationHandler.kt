package example.conversation

import io.chengine.command.HandleCommand
import io.chengine.connector.BotRequestContext
import io.chengine.dsl.sendTelegramMessage
import io.chengine.graph.ConversationGraph
import io.chengine.graph.GraphTransition
import io.chengine.handler.Handler
import io.chengine.handler.TelegramHandleText
import io.chengine.session.Conversations
import org.springframework.stereotype.Component

@Handler
@Component
open class ConversationHandler {
    private val graph = ConversationGraph("start", mapOf(
        "start" to { _, _ -> GraphTransition.Await("name", "你叫什么名字？发送 /cancel 取消。") },
        "name" to { session, input ->
            val name = input.orEmpty().trim()
            if (name.isEmpty()) GraphTransition.Await("name", "请发送非空回复。")
            else {
                session["name"] = name
                GraphTransition.Await("city", "你在哪个城市？可以输入 “跳过” 跳过。")
            }
        },
        "city" to { session, input ->
            val city = input.orEmpty().trim()
            when {
                city.isEmpty() -> GraphTransition.Await("city", "请发送非空回复。")
                city.equals("跳过", ignoreCase = true) -> GraphTransition.Next("withoutCity")
                else -> { session["city"] = city; GraphTransition.Next("withCity") }
            }
        },
        "withCity" to { session, _ -> GraphTransition.Finish("很高兴认识你，${session["name"]}！你所在的城市是：${session["city"]}。") },
        "withoutCity" to { session, _ -> GraphTransition.Finish("很高兴认识你，${session["name"]}！") }
    ))

    @HandleCommand("/start")
    open fun start(request: BotRequestContext) = sendTelegramMessage {
        text = if (request.sessionKey() == null) "无法确定用户和聊天。"
        else graph.start(Conversations.start(request, "start")).message
    }

    @HandleCommand("/cancel")
    open fun cancel() = sendTelegramMessage {
        val active = Conversations.current() != null
        Conversations.end()
        text = if (active) "对话已取消。" else "没有正在进行的对话。"
    }

    @TelegramHandleText
    open fun answer(request: BotRequestContext) = sendTelegramMessage {
        val session = Conversations.current()
        text = if (session == null) "请使用 /start 开始对话。"
        else {
            val result = graph.resume(session, request.textContent().orEmpty())
            if (result.completed) Conversations.end()
            result.message
        }
    }
}
