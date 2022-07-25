package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.core.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LinearCommandTest {
    @Test
    fun commandExecutionOrderTest() {
        val manager = MockManager()
        val linear = LinearCommand(listOf(MockCommand(4), MockCommand(4), MockCommand(4)))

        linear.setManager(manager)

        linear.init()

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        linear.start(frame)

        repeat(6) {
            if (!linear.done) {
                frame = Frame.from(Duration.ZERO, frame)
                linear.update(frame)
            }
        }

        frame = Frame.from(Duration.ZERO, frame)
        linear.cleanup()

        assertArrayEquals(linear.list.map { true }.toTypedArray(), linear.list.map { (it as MockCommand).state.isSafe }.toTypedArray())
    }
}