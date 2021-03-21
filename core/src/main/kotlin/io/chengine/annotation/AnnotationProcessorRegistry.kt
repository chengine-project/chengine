package io.chengine.annotation

interface AnnotationProcessorRegistry {
    val processors: List<AnnotationProcessor>
}