package io.chengine.message

interface MessageProcessorAware<T, G> {

    fun set(messageProcessor: MessageProcessor<T, G>)

}