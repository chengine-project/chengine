package io.chengine

import io.chengine.connector.AbstractBot

data class ChengineConfig(val handlers: List<Any>?, val bots: List<AbstractBot>)