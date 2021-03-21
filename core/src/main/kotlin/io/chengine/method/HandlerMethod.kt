package io.chengine.method

import org.apache.logging.log4j.kotlin.logger
import java.lang.reflect.InvocationTargetException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.cast
import kotlin.reflect.full.functions

/**
 * Wrap around [java.lang.reflect.Method]
 */
data class HandlerMethod(val handler: Any, val method: KFunction<*>) {

    val logger = logger()

    init {
        val contains = handler::class.functions.contains(method)
        if (!contains) {
            throw NoSuchMethodException("Method with name `${method.name}`not found in `${handler.javaClass.name}`")
        }
    }

    /**
     * Invokes handler method and try to cast returned object to a specific type
     */
    fun <T : Any> invoke(clazz: KClass<T>, params: Array<Any?>): T? {
        try {
            return method.call(handler, *params)?.let {
                clazz.cast(it)
            }
        } catch (ex: Exception) {
            when (ex) {
                is InvocationTargetException,
                is IllegalAccessException -> {
                    throw MethodInvocationException(ex)
                }
                else -> throw ex
            }
        }
    }
}