package io.chengine.handler

import io.chengine.connector.RequestExtractor
import kotlin.reflect.KClass

interface SingleHandlerAnnotationExtractor : RequestExtractor<KClass<out Annotation>>