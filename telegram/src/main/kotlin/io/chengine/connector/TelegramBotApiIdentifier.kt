package io.chengine.connector

class TelegramBotApiIdentifier: BotApiIdentifier {

    companion object {
        val instance = TelegramBotApiIdentifier()
    }

    override fun identifier(): String {
        return "telegram"
    }
}