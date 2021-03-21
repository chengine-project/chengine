package io.chengine.command

/**
 * Adds common prefix for all [HandleCommand] in [io.chengine.handler.Handler]
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandMapping(val prefix: String)
