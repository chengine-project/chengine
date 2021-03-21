package io.chengine.pipeline

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Stage(val value: Int, val name: String = "")
