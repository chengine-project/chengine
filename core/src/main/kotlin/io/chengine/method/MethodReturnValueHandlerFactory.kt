package io.chengine.method

import io.chengine.connector.Bot
import io.chengine.connector.Factory
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

inline fun <reified T : Any> MethodReturnValueHandlerFactory.get(): MethodReturnValueHandler<*>? = get(T::class)

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
                logger.info {
                    "Method return value registered `${it::class.simpleName}` for type `${it.support().simpleName}`"
                }
                map[it.support()] = it
            }

    override fun get(clazz: KClass<*>): MethodReturnValueHandler<*>? {
        if (map[clazz] == null) {
            synchronized(this) {
                if (map[clazz] == null) {
                    map.keys.forEach { c ->
                        // TODO переделать алгоритм для поиска самого ближайшего родственника
                        if (clazz.isSubclassOf(c)) {
                            map[c]?.let {
                                map[clazz] = it
                                logger.info {
                                    "Method return value registered lazily `${it::class.simpleName}` for type `${clazz.simpleName}`"
                                }
                            }
                        }
                    }
                }
            }
        }

        return map[clazz]
    }

}