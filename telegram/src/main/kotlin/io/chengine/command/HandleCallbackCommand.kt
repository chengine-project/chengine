package io.chengine.command

/**
 * Handle a command (if recognized) from inline keyboard callback
 *
 * @see Command
 * @see HandleCommand
 * @see CommandParameter
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class HandleCallbackCommand(val command: String)
