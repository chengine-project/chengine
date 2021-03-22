package io.chengine.connector

import kotlin.reflect.KClass

inline fun <reified T : Any> BotResponseContext.get() = get(T::class)

interface BotResponseContext {

    fun currentResponseObject(): Any?

    fun <T : Any> get(clazz: KClass<T>): T?

}