package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethod

inline fun async(block: AsyncBuilder.() -> Unit): BotApiMethod<*> = TODO()

class AsyncBuilder {

    fun easyScope(block: () -> BotApiMethod<*>): Unit = TODO()

    fun hardWorkingScope(): Unit = TODO()
}