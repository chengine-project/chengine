package io.chengine.connector

import io.chengine.command.Command
import io.chengine.session.SessionKey
import kotlin.reflect.KClass

open class DefaultBotRequestContext : BotRequestContext {

    val contextMap = HashMap<KClass<*>, Any?>()
    var botApiIdentifier: BotApiIdentifier? = null
    var sessionKey: SessionKey? = null
    var singleHandlerAnnotation: KClass<out Annotation>? = null
    var command: Command? = null

    override fun apiIdentifier(): BotApiIdentifier {
        return botApiIdentifier ?: throw RuntimeException("Bot api identifier wasn't set")
    }

    override fun <T : Any> get(clazz: KClass<T>): T? {
        return contextMap[clazz]?.let {
            @Suppress("UNCHECKED_CAST")
            it as T
        }
    }

    override fun sessionKey(): SessionKey? {
        return sessionKey
    }

    override fun singleHandler(): KClass<out Annotation>? {
        return singleHandlerAnnotation
    }

    override fun command(): Command? {
        return command
    }
}