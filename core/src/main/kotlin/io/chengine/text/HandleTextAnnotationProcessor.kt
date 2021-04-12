package io.chengine.text

import io.chengine.annotation.AnnotationProcessor
import io.chengine.handler.DefaultHandlerRegistry
import io.chengine.method.HandlerMethod
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

@Component
class HandleTextAnnotationProcessor(
    private val handlerRegistry: DefaultHandlerRegistry,
) : AnnotationProcessor {

    private val logger = logger()

    override fun process(handler: Any) {
        handler::class.declaredMemberFunctions.forEach { function ->
            function.findAnnotation<HandleText>()?.let { handleText ->
                val handleMethod = HandlerMethod(handler, function)
                val handleTextValue = handleText.value
                if (handleTextValue != "") {
                    if (handlerRegistry.containsBinding(handleTextValue)) {
                        throw RuntimeException("Duplicated binding `$handleTextValue`")
                    }
                    handlerRegistry.putTextBinding(handleTextValue, handleMethod)
                    logger.info { "Text binding registered: `$handleTextValue`" }
                }
                handleText.alternateValues.forEach { textCommand ->
                    if (handlerRegistry.containsBinding(textCommand)) {
                        throw RuntimeException("Duplicated binding `$textCommand`")
                    }
                    handlerRegistry.putTextBinding(textCommand, handleMethod)
                    logger.info { "Text binding registered: `$textCommand`" }
                }
            }
        }
    }
}