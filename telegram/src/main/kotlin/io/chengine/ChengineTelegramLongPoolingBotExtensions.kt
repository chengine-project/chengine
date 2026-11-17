package io.chengine

import io.chengine.connector.DefaultBotResponseContext
import org.telegram.telegrambots.meta.api.methods.botapimethods.PartialBotApiMethod

fun ChengineTelegramLongPoolingBot.execute(method: () -> PartialBotApiMethod<*>) {
    val botResponseContext = DefaultBotResponseContext()
    botResponseContext.currentResponseObject = method.invoke()
    this.executeResponse(botResponseContext)
}