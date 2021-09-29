package io.chengine.dsl

import org.telegram.telegrambots.meta.api.methods.BotApiMethod

inline fun async(block: AsyncBuilder.() -> Unit): BotApiMethod<*> = TODO()

class AsyncBuilder {

    fun easyScope(block: () -> BotApiMethod<*>): Unit = TODO()

    fun hardWorkingScope(): Unit = TODO()
}