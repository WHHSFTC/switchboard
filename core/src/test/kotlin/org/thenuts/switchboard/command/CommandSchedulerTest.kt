package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.core.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandSchedulerTest {
    @Test
    fun orderTest() {
        val a = MockCommand(8)
        val b = MockCommand(8, prereqs = listOf(a))
        val c = MockCommand(8, prereqs = listOf(a), postreqs = listOf(b))

        val sched = CommandScheduler()

        sched.addCommand(b)
        sched.addCommand(c)
        sched.addCommand(a)

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        repeat(4) {
            frame = Frame.from(Duration.ZERO, frame)
            sched.update(frame)
        }

        assertArrayEquals(listOf(a, c, b).toTypedArray(), sched.nodes.filterIsInstance<CommandScheduler.Node.CommandNode>().map { it.cmd }.toTypedArray())

        sched.clear()

        assertArrayEquals(sched.nodes.map { true }.toTypedArray(), sched.nodes.map {
            (it as? CommandScheduler.Node.CommandNode)?.let { (it.cmd as MockCommand).state.isSafe } ?: true
        }.toTypedArray())
    }
}