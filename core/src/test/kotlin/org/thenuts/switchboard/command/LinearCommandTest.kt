package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.command.combinator.LinearCommand
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinearCommandTest {
    @Test
    fun interruptTest() {
        val linear = LinearCommand(listOf(MockCommand(4), MockCommand(4), MockCommand(4)))

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        linear.start(frame)

        repeat(6) {
            if (!linear.done) {
                frame = Frame.from(Duration.ZERO, frame)
                linear.update(frame)
            }
        }

        linear.cleanup()

        assertArrayEquals(linear.list.map { true }.toTypedArray(), linear.list.map { (it as MockCommand).state.isSafe }.toTypedArray())
    }
}