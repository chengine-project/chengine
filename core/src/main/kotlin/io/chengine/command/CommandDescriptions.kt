package io.chengine.command

/**
 * Add descriptions to a command
 *
 * @see CommandDescription
 * @author Ilya Mikheev
 */
@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class CommandDescriptions(val value: Array<CommandDescription>)