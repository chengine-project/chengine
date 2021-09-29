package io.chengine.connector

import kotlin.reflect.KClass



class DefaultBotResponseContext : BotResponseContext {

    lateinit var currentResponseObject: Any
    val contextMap: HashMap<KClass<Any>, Any> = hashMapOf()

    override fun currentResponseObject(): Any? = currentResponseObject

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(clazz: KClass<T>): T? = contextMap[clazz as KClass<Any>] as? T

}