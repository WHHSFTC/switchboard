package org.thenuts.switchboard.command.atomic

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

/**
 * Command that manages a cooperatively scheduled suspending function.
 *
 * The function [run] has access to the contents of a [LinearContext], which includes the current
 * frame as [LinearContext.frame].
 *
 * To complete an update cycle, call [LinearContext.yield] and the function will be re-entered next
 * update.
 */
class LinearCommand(
    val run: suspend LinearContext.() -> Unit,
    val finally: () -> Unit
) : Command {
    lateinit var runner: LinearRunner

    override var done: Boolean = false

    override fun start(frame: Frame) {
        runner = LinearRunner(frame)
        runner.nextStep = run.createCoroutineUnintercepted(receiver = runner, completion = runner)
    }

    override fun update(frame: Frame) {
        val step = runner.nextStep
        if (step == null) {
            done = true
            return
        }
        runner.nextStep = null
        runner.frame = frame
        step.resume(Unit)

        if (runner.nextStep == null) {
            done = true
        }
    }

    override fun cleanup() {
        finally()
    }
}

class LinearRunner(
    override var frame: Frame
) : Continuation<Unit>, LinearContext() {
    var nextStep: Continuation<Unit>? = null

    override suspend fun yield() {
        suspendCoroutineUninterceptedOrReturn<Unit> { c ->
            nextStep = c
            COROUTINE_SUSPENDED
        }
    }

    // Completion continuation implementation
    override fun resumeWith(result: Result<Unit>) {
        result.getOrThrow() // just rethrow exception if it is there

    }
    override val context: CoroutineContext
        get() = EmptyCoroutineContext
}

abstract class LinearContext {
    abstract val frame: Frame
    abstract suspend fun yield()

    suspend fun Command.run() {
        start(frame)

        if (done) {
            cleanup()
            return
        }

        while (true) {
            update(frame)
            if (done) break
            yield()
        }

        cleanup()
    }
}
