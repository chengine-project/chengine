package io.chengine.command

import org.springframework.stereotype.Component

@Component
class DefaultCommandValidator: CommandValidator {

    companion object {
        val instance = DefaultCommandValidator()
        const val MAX_COMMAND_LENGTH = 64
    }

    private val commandPattern = "(/[a-zA-Z0-9\\-_]+(#([a-zA-Z0-9\\-_.,\$@&]+)?)?)+".toRegex()

    override fun isCommand(s: String): Boolean = s.trim().startsWith("/")

    override fun validate(command: String) {
        if (isCommand(command)) {
            if (command.trim() == "/") {
                throw CommandValidationException("Command is empty")
            }
            if (command.substring(1).length > MAX_COMMAND_LENGTH) {
                throw CommandValidationException("Command length more than $MAX_COMMAND_LENGTH symbols")
            }
            if (!command.matches(commandPattern)) {
                throw CommandValidationException("Command doesn't match pattern `${commandPattern.pattern}`")
            }
        } else {
            if (command.isBlank()) {
                throw CommandValidationException("Input string is empty")
            } else {
                throw CommandValidationException("`$command` is not a command")
            }
        }
    }
}