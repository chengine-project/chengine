package io.chengine.command

/**
 * Handles a text as a command.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HandleText(val text: Array<String>)
