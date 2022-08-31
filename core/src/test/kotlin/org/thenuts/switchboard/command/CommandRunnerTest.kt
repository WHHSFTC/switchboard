package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandRunnerTest {
    @Test
    fun lifecycleTest() {
        val command = MockCommand(4)
        val runner = CommandRunner(command)
        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        while (!runner.step(frame)) {
            frame = Frame.from(Duration.ZERO, frame)
        }

        assertEquals(MockCommand.State.CLEANUP, command.state)
    }

    @Test
    fun oneCycleTest() {
        val command = MockCommand(1)
        val runner = CommandRunner(command)
        val frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        val done = runner.step(frame)

        assertEquals(MockCommand.State.CLEANUP, command.state)
        assert(done)
    }

    @Test
    fun interruptTest() {
        val command = MockCommand(4)
        val runner = CommandRunner(command)
        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        repeat(2) {
            runner.step(frame)
            frame = Frame.from(Duration.ZERO, frame)

            assertEquals(MockCommand.State.UPDATE, command.state)
        }

        runner.interrupt()

        assertEquals(MockCommand.State.CLEANUP, command.state)
    }
}