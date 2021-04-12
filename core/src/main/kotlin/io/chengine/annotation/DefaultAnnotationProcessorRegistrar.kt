package io.chengine.annotation

import org.springframework.stereotype.Component

@Component
class DefaultAnnotationProcessorRegistrar(annotationProcessors: List<AnnotationProcessor>) :
    AnnotationProcessorRegistrar,
    AnnotationProcessorRegistry {

    override val processors: List<AnnotationProcessor>
        get() = mutableAnnotationProcessorsList

    private var mutableAnnotationProcessorsList = ArrayList(annotationProcessors)

    override fun registerAnnotationProcessors(processors: List<AnnotationProcessor>) {
        processors.forEach {
            mutableAnnotationProcessorsList.add(it)
        }
    }
}