package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.dsl.mkSwitch
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SwitchCommandTest {
    @Test
    fun lifecycleTest() {
        val manager = MockManager()

        fun nTest(n: Int) {
//            val switch = SwitchCommand({ n }, listOf(
//                SwitchCommand.Case({ it < 1 }, MockCommand(4)),
//                SwitchCommand.Case({ it > 1 }, MockCommand(4)),
//                SwitchCommand.Case({ true }, MockCommand(4)),
//            ))
            var x = 0
            var c: MockCommand? = null
            val switch = manager.newCommand(mkSwitch({ n }) {
                satisfies({ it < 1 }) {
                    task { x = 0 }
                    add { MockCommand(4).also { c = it } }
                }

                satisfies({ it > 1 }) {
                    task { x = 1 }
                    add { MockCommand(4).also { c = it } }
                }

                default {
                    task { x = 2 }
                    add { MockCommand(4).also { c = it } }
                }
            }) as SwitchCommand<Int>

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
            assertArrayEquals(listOf(x == 0, x == 1, x == 2).toTypedArray(), switch.cases.map { !seen && it.pred(n).also { seen = it } }.toTypedArray())
            assertTrue(c!!.state.isSafe)
        }

        nTest(0)
        nTest(2)
        nTest(1)
    }
}