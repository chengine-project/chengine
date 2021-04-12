package io.chengine.handler

import io.chengine.annotation.AnnotationProcessorRegistry
import org.apache.logging.log4j.kotlin.logger
import org.springframework.stereotype.Component
import kotlin.reflect.full.findAnnotation

@Component
class DefaultHandlerRegistrar(
    private val annotationProcessorRegistry: AnnotationProcessorRegistry,
) : HandlerRegistrar {

    private val logger = logger()

    override fun register(handler: Any) {
        handler::class.findAnnotation<Handler>()?.let {
            logger.info { "Start processing handler `${it::class.qualifiedName}`" }
            annotationProcessorRegistry.processors.forEach {
                it.process(handler)
            }
        } ?: run {
            logger.warn {
                "Class `${handler::class.simpleName}` not annotated by @Handler annotation, skip processing"
            }
        }
    }
}