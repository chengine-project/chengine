package io.chengine.pipeline

import io.chengine.handler.Handler
import java.util.concurrent.TimeUnit

@Handler
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Pipeline(val inactiveTimeout: Int = 60_000, val timeUnits: TimeUnit = TimeUnit.MILLISECONDS)
