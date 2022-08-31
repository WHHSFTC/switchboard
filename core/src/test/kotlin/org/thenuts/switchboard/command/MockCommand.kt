package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.*
import org.thenuts.switchboard.util.Frame

class MockCommand(
    var n: Int,
    override val prereqs: List<Pair<Command, Int>> = listOf(),
    override val postreqs: List<Pair<Command, Int>> = listOf(),
) : Command {
    enum class State(val isSafe: Boolean = false) {
        FIRST(true),
        START,
        UPDATE,
        CLEANUP(true)
    }

    var state = State.FIRST

    override var done: Boolean = false

    override fun start(frame: Frame) {
        super.start(frame)
        assertEquals(State.FIRST, state, "start() should be called first, not after ${state}")
        state = State.START
    }

    override fun update(frame: Frame) {
        super.update(frame)
        assertFalse(done)
        assertTrue(state == State.START || state == State.UPDATE, "update() should be called after start() or update(), not ${state}")
        state = State.UPDATE

        n--
        if (n == 0) {
            done = true;
        }
    }

    override fun cleanup() {
        super.cleanup()
        assertTrue(state == State.START || state == State.UPDATE, "cleanup() should be called after start() or update(), not ${state}")
        state = State.CLEANUP
    }
}