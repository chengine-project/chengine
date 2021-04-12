package io.chengine.handler

import io.chengine.annotation.AnnotationProcessor
import io.chengine.common.then
import io.chengine.method.HandlerMethod
import org.apache.logging.log4j.kotlin.logger
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions

abstract class AbstractSingleHandlerAnnotationProcessor: AnnotationProcessor, HandlerRegistryAware {

    private val logger = logger()

    private lateinit var handlerRegistry: DefaultHandlerRegistry

    abstract fun support(): Set<KClass<out Annotation>>

    override fun process(handler: Any) {
        support().forEach { annotation ->
            handler::class.declaredMemberFunctions.forEach { method ->
                method
                    .annotations.map { it::class }
                    .contains(annotation)
                    .then {
                        handlerRegistry.putSingleHandler(annotation, HandlerMethod(handler, method))
                        logger.info { "Single handler registered: `${annotation.simpleName}`" }
                    }
            }
        }
    }

    override fun set(handlerRegistry: HandlerRegistry) {
        this.handlerRegistry = handlerRegistry as DefaultHandlerRegistry
    }
}