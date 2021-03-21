package io.chengine.command

import org.springframework.stereotype.Component

@Component
class DefaultCommandParser : CommandParser {

    companion object {
        val instance = DefaultCommandParser()
    }

    override fun parse(s: String): Command {
        DefaultCommandValidator.instance.validate(s)
        val commandPartMap = LinkedHashMap<String, String?>()
        val iterator = CommandIterator(s)
        while (iterator.hasNext()) {
            val part = iterator.next()
            if (part.contains("#")) {
                val param = part.substring(0, part.indexOf("#"))
                if (commandPartMap.containsKey(param)) {
                    throw CommandParsingException("Duplicated parameter `$param` in `$s`")
                }
                val value = part.substring(part.indexOf("#") + 1)
                commandPartMap[param] = value
            } else {
                commandPartMap[part] = null
            }
        }

        return Command.from(commandPartMap)
    }
}