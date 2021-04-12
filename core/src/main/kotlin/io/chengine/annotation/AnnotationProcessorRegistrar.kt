package io.chengine.annotation

interface AnnotationProcessorRegistrar {

    fun registerAnnotationProcessors(processors: List<AnnotationProcessor>)

}