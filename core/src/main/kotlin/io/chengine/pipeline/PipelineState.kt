package io.chengine.pipeline

import java.util.concurrent.atomic.AtomicInteger

data class PipelineState(val pipelineDefinition: PipelineDefinition) {

    private val currentStage = AtomicInteger()

    init {
        currentStage.set(0)
    }

    /**
     * Returns tuple of current stage index and all of stages amount
     */
    fun currentStage(): Pair<Int, Int> = currentStage.get() to pipelineDefinition.stageDefinitions.size - 1

    fun upStage() {
        synchronized(currentStage) {
            val stepIndex = currentStage.get()
            val stepsListSize = pipelineDefinition.stageDefinitions.size
            if (stepsListSize > stepIndex + 1) {
                currentStage.incrementAndGet()
            }

            throw RuntimeException("Can't up stage because last stage achieved")
        }
    }

}