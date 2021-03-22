package io.chengine.autoconfigure

import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.getBeansOfType
import org.springframework.context.ApplicationContext
import kotlin.reflect.KClass

inline fun <reified T : Annotation> ListableBeanFactory.getAllBeansAnnotatedBy(): List<Any> =
    getBeansWithAnnotation(T::class.java)
        .map { entry -> entry.value }
        .toList()

inline fun <reified T : Any> ListableBeanFactory.getAllBeansOfType(): List<T> =
    getBeansOfType<T>()
        .map { entry -> entry.value }
        .toList()