package io.chengine.command

interface CommandParser {

    /**
     * Parse string representation of a command to [Command] object
     *
     * @param s command path
     * @throws CommandValidationException when command pattern validation failed (look at [CommandValidator])
     * @throws CommandParsingException when command parameter duplicated
     */
    fun parse(s: String): Command

}