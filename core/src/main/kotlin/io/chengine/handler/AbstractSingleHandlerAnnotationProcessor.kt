package io.chengine.handler

import io.chengine.annotation.AnnotationProcessor
import io.chengine.common.then
import io.chengine.method.HandlerMethod
import org.apache.logging.log4j.kotlin.logger
import java.lang.reflect.Proxy
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberFunctions
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

abstract class AbstractSingleHandlerAnnotationProcessor(val handlerRegistry: DefaultHandlerRegistry) :
    AnnotationProcessor, HandlerRegistryAware {

    private val logger = logger()

    abstract fun support(): Set<KClass<out Annotation>>

    override fun process(handler: Any) {
        support().forEach { annotation ->
            handler::class
                .memberFunctions
                .filter { it.isOpen }
                .forEach { function ->
                    function
                        .annotations
                        .map { it.annotationClass }
                        .contains(annotation)
                        .then {
                            handlerRegistry.putSingleHandler(annotation, HandlerMethod(handler, function))
                            logger.info { "Single handler registered: `${annotation.simpleName}`" }
                        }
                }
        }
    }

    override fun set(handlerRegistry: HandlerRegistry) {
//        this.handlerRegistry = handlerRegistry as DefaultHandlerRegistry
    }
}