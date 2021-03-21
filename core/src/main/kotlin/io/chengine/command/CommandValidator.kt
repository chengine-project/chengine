package io.chengine.command

interface CommandValidator {

    /**
     * Defines if input string is a command
     *
     */
    fun isCommand(s: String): Boolean

    /**
     * Validate a string representation of command
     *
     * @param command command for validating
     * @throws CommandValidationException
     */
    fun validate(command: String)

}