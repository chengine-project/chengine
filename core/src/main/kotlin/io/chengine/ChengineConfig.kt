package io.chengine

import io.chengine.connector.AbstractBot
import io.chengine.session.SessionStore

data class ChengineConfig(val handlers: List<Any>?, val bots: List<AbstractBot>, val sessionStore: SessionStore? = null)