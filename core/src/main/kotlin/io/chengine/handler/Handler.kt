package io.chengine.handler

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Handler(val name: String = "")
