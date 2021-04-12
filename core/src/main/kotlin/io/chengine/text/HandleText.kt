package io.chengine.text

/**
 * Handles a text as a command.
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HandleText(val value: String = "", val alternateValues: Array<String> = [])
