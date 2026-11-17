package io.chengine

import example.conversation.ConversationHandler
import io.chengine.connector.BotResponseContext
import io.chengine.session.UserSessionContextHolder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.chat.Chat
import org.telegram.telegrambots.meta.api.objects.message.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User

private class ConversationBot : ChengineTelegramLongPoolingBot() {
    var result: String? = null
    override fun executeResponse(response: BotResponseContext) { result = (response.currentResponseObject() as SendMessage).text }
}

class ConversationProcessingTest {
    private fun bot() = ConversationBot().also { Chengine(ChengineConfig(listOf(ConversationHandler()), listOf(it))) }
    private fun send(bot: ConversationBot, text: String, user: Long = 1L, chatId: Long = 10L): String? {
        val update = Update().apply { message = Message().apply {
            this.text = text
            chat = Chat(chatId, "private")
            from = User(user, "Test", false)
        } }
        bot.process(update)
        assertNull(UserSessionContextHolder.currentSession())
        return bot.result
    }
    @Test fun `collects name and city and then clears session`() {
        val bot = bot()
        assertTrue(send(bot, "/start")!!.contains("зовут"))
        assertTrue(send(bot, "Аня")!!.contains("городе"))
        assertEquals("Приятно познакомиться, Аня! Ваш город: Москва.", send(bot, "Москва"))
        assertTrue(send(bot, "дальше")!!.contains("/start"))
    }
    @Test fun `isolates users and chats and cancels only one session`() {
        val bot = bot()
        send(bot, "/start", 1, 10); send(bot, "/start", 2, 10); send(bot, "/start", 1, 20)
        send(bot, "Аня", 1, 10); send(bot, "Борис", 2, 10); send(bot, "Вера", 1, 20)
        assertEquals("Диалог отменён.", send(bot, "/cancel", 1, 10))
        assertTrue(send(bot, "Москва", 1, 10)!!.contains("/start"))
        assertTrue(send(bot, "Казань", 2, 10)!!.contains("Борис"))
        assertTrue(send(bot, "Томск", 1, 20)!!.contains("Вера"))
    }
    @Test fun `restart replaces previous conversation`() {
        val bot = bot()
        send(bot, "/start"); send(bot, "Старое имя"); send(bot, "/start")
        assertTrue(send(bot, "Новое имя")!!.contains("городе"))
        assertTrue(send(bot, "Омск")!!.contains("Новое имя"))
    }
}
