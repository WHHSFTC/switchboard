package org.thenuts.switchboard.command

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.command.atomic.LinearCommand
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinearCommandTest {
    @Test
    fun basicTest() {
        val cmd = LinearCommand(
            run = { yield(); yield() },
            finally = { }
        )

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        cmd.start(frame)
        while (!cmd.done) {
            cmd.update(frame)
            frame = Frame.from(Duration.ZERO, frame)
        }
        cmd.cleanup()

        assert(true)
    }

    @Test
    fun runSubCommand() {
        val mock = MockCommand(5)

        val cmd = LinearCommand(
            run = { mock.run() },
            finally = { }
        )

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        cmd.start(frame)
        while (!cmd.done) {
            cmd.update(frame)
            frame = Frame.from(Duration.ZERO, frame)
        }
        cmd.cleanup()

        assert(mock.state.isSafe)
        assert(mock.done)
    }
}