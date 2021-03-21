package io.chengine.interceptor

interface RequestInterceptorChainFactory {

    fun get(): RequestInterceptorChain

}