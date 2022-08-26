package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.command.combinator.SwitchCommand
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SwitchCommandTest {
    @Test
    fun lifecycleTest() {
        fun nTest(n: Int) {
            val switch = SwitchCommand({ n }, listOf(
                SwitchCommand.Case({ it < 1 }, MockCommand(4)),
                SwitchCommand.Case({ it > 1 }, MockCommand(4)),
                SwitchCommand.Case({ true }, MockCommand(4)),
            ))

            var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
            switch.start(frame)

            repeat(5) {
                if (!switch.done) {
                    frame = Frame.from(Duration.ZERO, frame)
                    switch.update(frame)
                }
            }

            switch.cleanup()

            var seen = false
            assertArrayEquals(switch.cases.map { !seen && it.pred(n).also { seen = it } }.toTypedArray(), switch.cases.map { it.command.done }.toTypedArray())
            assertArrayEquals(switch.cases.map { true }.toTypedArray(), switch.cases.map { (it.command as MockCommand).state.isSafe }.toTypedArray())
        }

        nTest(0)
        nTest(2)
        nTest(1)
    }
}