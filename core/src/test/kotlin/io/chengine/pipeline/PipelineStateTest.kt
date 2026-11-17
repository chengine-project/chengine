package io.chengine.pipeline

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class PipelineStateTest {
    @Test fun `advances until final stage then rejects further progress`() {
        val state = PipelineState(PipelineDefinition(PipelineStateTest::class, 1, TimeUnit.MINUTES,
            listOf(StageDefinition(0), StageDefinition(1))))
        assertEquals(0 to 1, state.currentStage())
        state.upStage()
        assertEquals(1 to 1, state.currentStage())
        assertThrows(RuntimeException::class.java) { state.upStage() }
        assertEquals(1 to 1, state.currentStage())
    }
}
