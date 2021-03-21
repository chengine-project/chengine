package io.chengine.method

import io.chengine.connector.Bot
import io.chengine.connector.Factory
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

interface MethodReturnValueHandlerFactory : Factory {
    fun get(clazz: KClass<*>): MethodReturnValueHandler<*>?
}

@Component
class DefaultMethodReturnValueHandlerFactory : MethodReturnValueHandlerFactory {

    private val logger = logger()

    private val map = HashMap<KClass<*>, MethodReturnValueHandler<*>>()

    override fun put(bot: Bot) = bot
            .methodReturnValueHandlers()
            .forEach {
                logger.info { "Method return value registered `${it::class.simpleName}`" }
                map[it.support()] = it
            }

    override fun get(clazz: KClass<*>): MethodReturnValueHandler<*>? {
        if (map[clazz] == null) {
            synchronized(this) {
                if (map[clazz] == null) {
                    map.keys.forEach { c ->
                        clazz.isSubclassOf(c)
                        map[clazz] = map[c] as MethodReturnValueHandler<*>
                        logger.info { "Method return value registered lazily `${clazz.simpleName}`" }
                    }
                }
            }
        }

        return map[clazz]
    }

}