package io.chengine.pipeline

import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

data class PipelineDefinition(
    val clazz: KClass<*>,
    val inactiveTimeout: Int,
    val timeDurationUnit: TimeUnit,
    val stageDefinitions: List<StageDefinition>
)