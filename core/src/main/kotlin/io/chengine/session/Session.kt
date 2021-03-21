package io.chengine.session

import io.chengine.pipeline.PipelineState
import java.util.concurrent.TimeUnit

interface Session {

    fun sessionKey(): SessionKey

    fun expirationTime(): Pair<Int, TimeUnit>

    fun pipelineState(): PipelineState?

}