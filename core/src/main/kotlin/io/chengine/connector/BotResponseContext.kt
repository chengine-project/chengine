package io.chengine.connector

import kotlin.reflect.KClass

interface BotResponseContext {

    fun currentResponseObject(): Any?

    fun <T : Any> get(clazz: KClass<T>): T?

}