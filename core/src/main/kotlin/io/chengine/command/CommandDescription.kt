package io.chengine.command

/**
 * Add description to [HandleCommand] with specific localization
 *
 * @param value command description
 * @param localization localization parameter
 *
 * @author Andrey Borisov
 */
@Repeatable
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandDescription(val value: String, val localization: String = "")
