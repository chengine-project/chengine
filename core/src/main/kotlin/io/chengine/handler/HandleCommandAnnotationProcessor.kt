package io.chengine.handler

import io.chengine.annotation.AnnotationProcessor
import io.chengine.command.CommandMapping
import io.chengine.command.CommandValidator
import io.chengine.command.HandleCommand
import io.chengine.common.`else`
import io.chengine.common.then
import io.chengine.method.HandlerMethod
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

@Component
class HandleCommandAnnotationProcessor(
    private val handlerRegistry: DefaultHandlerRegistry,
    private val commandValidator: CommandValidator
) : AnnotationProcessor {

    private val logger = logger()
    private val commandPrefixRegex = "/[a-zA-z]+".toRegex()

    override fun process(handler: Any) {
        handler::class.findAnnotation<Handler>()?.let {
            var commandPrefix = ""
            handler::class.findAnnotation<CommandMapping>()?.let { commandMapping ->
                commandMapping.prefix.matches(commandPrefixRegex).then {
                    commandPrefix += commandMapping.prefix
                } `else` {
                    throw RuntimeException("Command prefix doesn't matches regex `${commandPrefixRegex.pattern}`")
                }
            }
            handler::class.declaredMemberFunctions.forEach { function ->
                var commandPath = "" + commandPrefix
                function.findAnnotation<HandleCommand>()?.let { handleCommand ->
                    try {
                        (handleCommand.command == "").then {
                            throw RuntimeException("Command annotation value can't be empty in class ${handler.javaClass.canonicalName}")
                        }
                        commandPath += handleCommand.command
                        commandValidator.validate(commandPath)
                        val handlerMethod = HandlerMethod(handler, function)
                        handlerRegistry.putCommand(commandPath, handlerMethod)
                        logger.info { "Command registered: `$commandPath`" }
                    } catch (ex: Exception) {

                    }
                }
            }
        } ?: run {
            logger.warn { "`@Handler` annotation not found on class ${handler::class.simpleName}, skip" }
        }
    }
}