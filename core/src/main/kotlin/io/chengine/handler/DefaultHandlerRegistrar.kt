package io.chengine.handler

import io.chengine.annotation.AnnotationProcessorRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DefaultHandlerRegistrar(
    private val annotationProcessorRegistry: AnnotationProcessorRegistry,
) : HandlerRegistrar {

    override fun register(handler: Any) {
        annotationProcessorRegistry.processors.forEach {
            it.process(handler)
        }
    }
}