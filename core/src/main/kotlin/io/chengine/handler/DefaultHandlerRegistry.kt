package io.chengine.handler

import io.chengine.command.Command
import io.chengine.method.HandlerMethod
import io.chengine.pipeline.PipelineDefinition
import org.springframework.stereotype.Component
import kotlin.reflect.KClass

@Component
class DefaultHandlerRegistry: HandlerRegistry {

    private val commandHandlerMap = HashMap<String, HandlerMethod>()
    private val singleHandlerMap = HashMap<KClass<out Annotation>, HandlerMethod>()
    private val pipelineDefinitionMap = HashMap<KClass<*>, PipelineDefinition>()
    private val textHandlerMap = HashMap<String, HandlerMethod?>()

    override fun getAllPath(): Set<String> {
        return commandHandlerMap.keys.toSet()
    }

    override fun getAllHandlerMethods(): Set<HandlerMethod> {
        return commandHandlerMap
            .values
            .toSet()
            .union(singleHandlerMap.values.toSet())
    }

    override fun getHandlerMethodBy(commandPath: String): HandlerMethod {
        return commandHandlerMap[commandPath] ?: textHandlerMap[commandPath]
        ?: throw NoSuchMethodException("Method `$commandPath` not found")
    }

    override fun getHandlerMethodBy(command: Command): HandlerMethod {
        return getHandlerMethodBy(command.path)
    }

    override fun getPipelineDefinitionBy(clazz: KClass<*>): PipelineDefinition {
        return pipelineDefinitionMap[clazz] ?: throw RuntimeException("Pipeline with class `${clazz.simpleName}` not found")
    }

    override fun getSingleHandlerBy(annotation: KClass<out Annotation>): HandlerMethod {
        return singleHandlerMap[annotation] ?: throw RuntimeException("Handler method for annotation `${annotation.simpleName}` not found")
    }

    fun putCommand(commandPath: String, handlerMethod: HandlerMethod) {
        commandHandlerMap[commandPath] = handlerMethod
    }

    fun putSingleHandler(annotation: KClass<out Annotation>, handlerMethod: HandlerMethod) {
        singleHandlerMap[annotation] = handlerMethod
    }
}