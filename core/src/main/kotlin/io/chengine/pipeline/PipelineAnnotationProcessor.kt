package io.chengine.pipeline

import io.chengine.annotation.AnnotationProcessor
import io.chengine.common.then
import io.chengine.handler.DefaultHandlerRegistry
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
open class PipelineAnnotationProcessor(
    @Autowired private val handlerRegistry: DefaultHandlerRegistry
) : AnnotationProcessor {

    override fun process(handler: Any) {
        handler.javaClass.isAnnotationPresent(Pipeline::class.java).then {

        }
    }
}