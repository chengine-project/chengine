package io.chengine.command

data class Command(
    val command: String,
    val path: String,
    val params: Map<String, String>
) {

    companion object {
        fun from(map: LinkedHashMap<String, String?>): Command {
            if (map.isEmpty()) {
                return empty()
            }
            val builder = builder()
            map.forEach { (k, v) -> builder.put(k, v) }
            return builder.build()
        }

        fun builder(): CommandBuilder {
            return CommandBuilder()
        }

        private fun empty(): Command {
            return Command("/", "/", emptyMap())
        }

        class CommandBuilder {

            private val parameterMap = LinkedHashMap<String, String?>()

            fun put(param: String): CommandBuilder {
                parameterMap[param] = null
                return this
            }

            fun put(param: String, value: String?): CommandBuilder {
                parameterMap[param] = value
                return this
            }

            fun build(): Command {
                val command = StringBuilder()
                val path = StringBuilder()
                val paramMap = HashMap<String, String>()
                if (parameterMap.isEmpty()) {
                    throw RuntimeException("Parameter map can't be empty")
                }

                parameterMap.forEach { (key, value) ->
                    command.append("/").append(key)
                    path.append("/").append(key)
                    value?.let {
                        command.append("#").append(it)
                        path.append("#")
                        paramMap[key] = it
                    }
                }

                return Command(command.toString(), path.toString(), paramMap)
            }
        }
    }

    fun getParam(key: String): String? = params[key]
}