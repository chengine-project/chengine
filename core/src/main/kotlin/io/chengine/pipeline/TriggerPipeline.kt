package io.chengine.pipeline

import io.chengine.message.ActionResponse
import kotlin.reflect.KClass

open class TriggerPipeline(val clazz: KClass<*>): ActionResponse