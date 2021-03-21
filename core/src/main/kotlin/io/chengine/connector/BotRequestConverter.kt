package io.chengine.connector

import io.chengine.common.Converter

interface BotRequestConverter<T> : Converter<T, BotRequestContext>