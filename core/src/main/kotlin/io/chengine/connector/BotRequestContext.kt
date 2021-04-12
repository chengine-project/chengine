package io.chengine.connector

import io.chengine.command.Command
import io.chengine.session.SessionKey
import kotlin.reflect.KClass

inline fun <reified T : Any> BotRequestContext.get(): T? = get(T::class)

interface BotRequestContext {

    /**
     * Api request received from
     */
    fun apiIdentifier(): BotApiIdentifier

    /**
     * Gets an object of required type from request context
     */
    fun <T : Any> get(clazz: KClass<T>): T?

    /**
     * Return a session key from request context
     */
    fun sessionKey(): SessionKey?

    /**
     * Returns a specific annotation class if request should
     * be handled by a `single handler`
     */
    fun singleHandler(): KClass<out Annotation>?

    /**
     * Returns a command if existed
     */
    fun command(): Command?

    /**
     * Returns a message text content if presented
     */
    fun textContent(): String?

}