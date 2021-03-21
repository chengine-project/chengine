package io.chengine.handler

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
public annotation class Handler(val name: String = "")
