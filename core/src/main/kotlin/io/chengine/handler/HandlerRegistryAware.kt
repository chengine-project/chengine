package io.chengine.handler

interface HandlerRegistryAware {

    fun set(handlerRegistry: HandlerRegistry)

}