package io.chengine.method

import io.chengine.connector.BotRequestContext
import io.chengine.handler.HandlerRegistry
import io.chengine.session.UserSessionContextHolder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DefaultHandlerMethodResolver(@Autowired private val handlerRegistry: HandlerRegistry) : HandlerMethodResolver {

    override fun resolve(request: BotRequestContext): HandlerMethod? {
        return UserSessionContextHolder.currentSession()?.let { session ->
            session.pipelineState()?.let {
                TODO("Add session handling")
            }
        } ?: run {
            request.command()?.let { command ->
                handlerRegistry.getHandlerMethodBy(command)
            }
            request.textContent()?.let { text ->
                handlerRegistry.getHandlerMethodBy(text)
            } ?: run {
                request.singleHandler()?.let { annotation ->
                    handlerRegistry.getSingleHandlerBy(annotation)
                }
            }
        }
    }
}