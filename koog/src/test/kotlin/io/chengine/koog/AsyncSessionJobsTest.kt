package io.chengine.koog

import example.koog.AsyncSessionJobs
import io.chengine.session.SessionKey
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class AsyncSessionJobsTest {
    @Test fun `submission returns promptly and independent chats can progress`() = runBlocking {
        val scope = CoroutineScope(coroutineContext + SupervisorJob(coroutineContext[Job]))
        val jobs = AsyncSessionJobs(scope)
        val first = SessionKey("1", "chat-a", "telegram", "bot")
        val second = SessionKey("2", "chat-b", "telegram", "bot")
        val entered = CompletableDeferred<Unit>()
        val gate = CompletableDeferred<Unit>()
        val otherFinished = CompletableDeferred<Unit>()
        try {
            assertTrue(jobs.submit(first) { entered.complete(Unit); gate.await() })
            assertFalse(jobs.submit(first) { error("duplicate must not execute") })
            assertTrue(jobs.submit(second) { otherFinished.complete(Unit) })
            withTimeout(1_000) { entered.await(); otherFinished.await() }
            assertFalse(gate.isCompleted)
            gate.complete(Unit)
        } finally {
            scope.cancel()
        }
    }
}
