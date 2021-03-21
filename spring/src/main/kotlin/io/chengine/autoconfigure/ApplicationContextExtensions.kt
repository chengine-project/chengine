package io.chengine.autoconfigure

import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass


fun ApplicationContext.getAllBeansAnnotatedBy(annotation: KClass<out Annotation>): List<Any> {
    return this.getBeansWithAnnotation(annotation.java)
        .map { entry ->  entry.value }
        .toList()
}

fun <T : Any> ApplicationContext.getAllBeansOfType(clazz: KClass<T>): List<T> {
    return this.getBeansOfType(clazz.java)
        .map { entry -> entry.value}
        .toList()
}