package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CommandSchedulerTest {
    private fun runTest(insertions: List<Command>, correctOrder: List<Command>): CommandScheduler {
        val sched = CommandScheduler()
        insertions.forEach { sched.addCommand(it) }

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)

        repeat(4) {
            frame = Frame.from(Duration.ZERO, frame)
            sched.update(frame)
        }

        assertArrayEquals(
            correctOrder.toTypedArray(),
            sched.nodes.filterIsInstance<CommandScheduler.CommandNode>().map { it.runner.cmd }
                .toTypedArray()
        )

        sched.clear()

        assertArrayEquals(sched.nodes.map { true }.toTypedArray(), sched.nodes.map {
            (it as? CommandScheduler.CommandNode)?.let { (it.runner.cmd as MockCommand).state.isSafe } ?: true
        }.toTypedArray())

        return sched
    }

    @Test
    fun orderTest() {
        val a = MockCommand(8)
        val b = MockCommand(8, prereqs = listOf(a to 1))
        val c = MockCommand(8, prereqs = listOf(a to 1), postreqs = listOf(b to 1))

        runTest(listOf(a, b, c), listOf(a, c, b))
    }

    @Test
    fun cycleTest() {
        val a = MockCommand(8)
        val b = MockCommand(8, prereqs = listOf(a to 1))
        val c = MockCommand(8, prereqs = listOf(b to 1), postreqs = listOf(a to 2))

        runTest(listOf(a, b, c), listOf(a, b, c))
    }
}