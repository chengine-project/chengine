package io.chengine.message

interface MessageProcessor<T, G> {

    fun process(request: T, response: G)

}