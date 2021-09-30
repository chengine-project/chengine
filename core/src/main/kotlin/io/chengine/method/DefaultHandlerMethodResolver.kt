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

            val command = request.command()
            if (command != null) {
                val handlerMethod = handlerRegistry.getHandlerMethodBy(command)
                if (handlerMethod != null) {
                    return handlerMethod
                }
            }

            val textContent = request.textContent()
            if (textContent != null) {
                val handlerMethod = handlerRegistry.getHandlerMethodBy(textContent)
                if (handlerMethod != null) {
                    return handlerMethod
                }
            }

            val singleHandler = request.singleHandler()
            if (singleHandler != null) {
                val handlerMethod = handlerRegistry.getSingleHandlerBy(singleHandler)
                if (handlerMethod != null) {
                    return handlerMethod
                }
            }

            return null
        }
    }
}