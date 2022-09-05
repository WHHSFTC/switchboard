package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.command.combinator.SlotCommand
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SlotCommandTest {
    @Test
    fun queueTest() {
        val slot = SlotCommand(
            prereqs = listOf(),
            postreqs = listOf()
        )

        val list = MutableList(4) { MockCommand(2) }
        list.forEach { slot.queue(it) }

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        slot.start(frame)

        repeat(10) {
            frame = Frame.from(Duration.ZERO, frame)
            slot.update(frame)
        }

        slot.cleanup()

        assertArrayEquals(list.map { true }.toTypedArray(), list.map { it.done }.toTypedArray())
        assertArrayEquals(list.map { true }.toTypedArray(), list.map { it.state.isSafe }.toTypedArray())
    }
    @Test
    fun interruptTest() {
        val slot = SlotCommand(
            prereqs = listOf(),
            postreqs = listOf()
        )

        val target = MockCommand(10)
        val replacement = MockCommand(2)

        slot.queue(target)

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        slot.start(frame)

        repeat(5) {
            frame = Frame.from(Duration.ZERO, frame)
            slot.update(frame)
        }

        slot.interrupt(replacement)

        repeat(5) {
            frame = Frame.from(Duration.ZERO, frame)
            slot.update(frame)
        }

        slot.cleanup()

        assert(target.state.isSafe)
        assert(!target.done)
        assert(replacement.state.isSafe)
        assert(replacement.done)
    }
}