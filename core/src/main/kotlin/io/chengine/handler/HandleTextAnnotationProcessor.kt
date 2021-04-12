package io.chengine.handler

import io.chengine.annotation.AnnotationProcessor
import io.chengine.command.CommandValidator
import io.chengine.command.HandleText
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

@Component
class HandleTextAnnotationProcessor(
    private val handlerRegistry: DefaultHandlerRegistry,
    private val commandValidator: CommandValidator
) : AnnotationProcessor {

    private val logger = logger()

    override fun process(handler: Any) {
        handler::class.findAnnotation<Handler>()?.let {
            handler::class.declaredMemberFunctions.forEach { function ->
                function.findAnnotation<HandleText>()?.let { handleText ->

                }
            }
        }
    }
}