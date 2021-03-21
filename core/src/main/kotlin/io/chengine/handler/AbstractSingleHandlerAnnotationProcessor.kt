package io.chengine.handler

import io.chengine.annotation.AnnotationProcessor
import io.chengine.method.HandlerMethod
import org.apache.logging.log4j.kotlin.logger
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation

abstract class AbstractSingleHandlerAnnotationProcessor: AnnotationProcessor, HandlerRegistryAware {

    private val logger = logger()

    private lateinit var handlerRegistry: DefaultHandlerRegistry

    abstract fun support(): Set<KClass<out Annotation>>

    override fun process(handler: Any) {
        support().forEach { annotation ->
            handler::class.declaredMemberFunctions.forEach { method ->
//                method::class.
//                if (method.isAnnotationPresent(annotation.java)) {
//                    handlerRegistry.putSingleHandler(annotation, HandlerMethod(handler, method))
//                    logger.info { "Single handler registered: `${annotation.simpleName}`" }
//                }
            }
        }
    }

    override fun set(handlerRegistry: HandlerRegistry) {
        this.handlerRegistry = handlerRegistry as DefaultHandlerRegistry
    }
}