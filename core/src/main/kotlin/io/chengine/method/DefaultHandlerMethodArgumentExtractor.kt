package io.chengine.method

import io.chengine.command.CommandParameter
import io.chengine.connector.BotRequestContext
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmErasure

@Component
class DefaultHandlerMethodArgumentExtractor : HandlerMethodArgumentExtractor {

    override fun extractArgumentsFrom(request: BotRequestContext, method: HandlerMethod): Array<Any?> {
        val parameters = method.method.parameters
        val args = if (parameters.size > 1) arrayOfNulls<Any?>(parameters.size - 1) else emptyArray()
        var i = 0
        parameters
            .drop(1)
            .forEach { parameter ->
                parameter.findAnnotation<CommandParameter>()?.let {
                    val paramName = it.value
                    val command = request.command()
                    val value = command?.getParam(paramName)
                    val typedValue = stringToType(value!!, parameter.type.jvmErasure)
                    args[i] = (typedValue)
                } ?: run {
                    args[i] = extractParameterWithType(request, parameter.type.jvmErasure)
                }

                i++
            }

        return args
    }

    private fun extractParameterWithType(request: BotRequestContext, clazz: KClass<*>): Any {
        if (clazz == BotRequestContext::class) {
            return request
        }

        return request.get(clazz) ?: run {

        }
    }

    private fun stringToType(s: String, clazz: KClass<*>): Any {
        if (clazz == String::class) {
            return s
        } else if (clazz == Int::class) {
            return s.toInt()
        } else if (clazz == Long::class) {
            return s.toLong()
        } else if (clazz == Float::class) {
            return s.toFloat()
        } else if (clazz == Double::class) {
            return s.toDouble()
        } else if (clazz == Boolean::class) {
            return s.toBoolean()
        } else {
            throw RuntimeException("Can't parse value: $s to type ${clazz.simpleName}")
        }
    }
}