package io.chengine.session

import io.chengine.pipeline.PipelineState
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/** Mutable state for a single conversation. Data is kept in memory. */
class ConversationSession(
    private val key: SessionKey,
    @Volatile var step: String,
    private val timeout: Int = 10,
    private val unit: TimeUnit = TimeUnit.MINUTES,
    initialValues: Map<String, String> = emptyMap()
) : Session {
    private val values = ConcurrentHashMap(initialValues)

    override fun sessionKey(): SessionKey = key
    override fun expirationTime(): Pair<Int, TimeUnit> = timeout to unit
    override fun pipelineState(): PipelineState? = null
    fun values(): Map<String, String> = values.toMap()
    operator fun get(name: String): String? = values[name]
    operator fun set(name: String, value: String) { values[name] = value }
}
