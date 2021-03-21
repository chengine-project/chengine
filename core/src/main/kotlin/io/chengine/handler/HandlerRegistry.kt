package io.chengine.handler

import io.chengine.command.Command
import io.chengine.method.HandlerMethod
import io.chengine.pipeline.PipelineDefinition
import kotlin.reflect.KClass

interface HandlerRegistry {

    fun getAllPath(): Set<String>

    fun getAllHandlerMethods(): Set<HandlerMethod>

    fun getHandlerMethodBy(commandPath: String): HandlerMethod

    fun getHandlerMethodBy(command: Command): HandlerMethod

    fun getPipelineDefinitionBy(clazz: KClass<*>): PipelineDefinition

    fun getSingleHandlerBy(annotation: KClass<out Annotation>): HandlerMethod

}