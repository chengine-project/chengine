package io.chengine.command

/**
 * An object which iterates through a command and claw parts of itself into
 * array. You can use [Iterator] methods for command passing.
 */
class CommandIterator(var command: String) : Iterator<String> {

    private var iterator: Iterator<String>

    init {
        val commandParts = ArrayList<String>()
        var command = command.substring(1)
        var continueParse = true
        while (continueParse) {
            val charArray = command.toCharArray()
            var sb = StringBuilder()
            for (i in charArray.indices) {
                val c = charArray[i]
                if (c != '/') {
                    sb.append(c)
                }
                if (c == '/' || i + 1 == charArray.size) {
                    commandParts.add(sb.toString())
                    sb = StringBuilder()
                }
                if (i + 1 == charArray.size) {
                    continueParse = false
                }
                if (c == '/') {
                    command = command.substring(i + 1)
                    break
                }
            }
        }
        iterator = commandParts.iterator()
    }

    override fun hasNext(): Boolean {
        return iterator.hasNext()
    }

    override fun next(): String {
        return iterator.next()
    }
}