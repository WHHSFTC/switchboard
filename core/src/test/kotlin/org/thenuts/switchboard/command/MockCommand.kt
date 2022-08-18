package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.*
import org.thenuts.switchboard.util.Frame

context(CommandContext)
class MockCommand(var n: Int, val prereqs: List<Command> = listOf(), val postreqs: List<Command> = listOf()) : CommandAbstract() {
    enum class State(val isSafe: Boolean = false) {
        NOTHING(true),
        START,
        UPDATE,
        CLEANUP(true)
    }

    var state = State.NOTHING

    override var done: Boolean = false

    override fun start(frame: Frame) {
        super.start(frame)
        assertEquals(State.NOTHING, state, "start() should be called first, not after ${state}")
        state = State.START

        prereqs.map { registerPrerequisite(it) }
        postreqs.map { registerPostrequisite(it) }
    }

    override fun update(frame: Frame) {
        super.update(frame)
        assertFalse(done, "update() should never be called when done is true")
        assertTrue(state == State.START || state == State.UPDATE, "update() should be called after start() or update(), not ${state}")
        state = State.UPDATE

        n--;
        if (n == 0) {
            done = true;
        }
    }

    override fun cleanup() {
        super.cleanup()
        assertTrue(state == State.START || state == State.UPDATE, "cleanup() should be called after start() or update(), not ${state}")
        state = State.CLEANUP

        prereqs.map { deregisterPrerequisite(it) }
        postreqs.map { deregisterPostrequisite(it) }
    }
}