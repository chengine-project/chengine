package io.chengine.annotation

interface AnnotationProcessorRegistrar {

    fun registerAnnotationProcessors(processors: List<AnnotationProcessor>)

    fun getAllProcessors(): List<AnnotationProcessor>

}