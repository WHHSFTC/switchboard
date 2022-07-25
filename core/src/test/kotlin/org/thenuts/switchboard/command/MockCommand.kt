package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.thenuts.switchboard.core.Frame

class MockCommand(var n: Int) : CommandAbstract() {
    enum class State(val isSafe: Boolean = false) {
        NOTHING(true),
        SET_MANAGER(true),
        INIT,
        START,
        UPDATE,
        CLEANUP(true)
    }

    var state = State.NOTHING

    override var done: Boolean = false

    override fun setManager(manager: CommandManager) {
        super.setManager(manager)
        assertEquals(State.NOTHING, state, "setManager() should be first function called on a Command")
        state = State.SET_MANAGER
    }

    override fun init() {
        super.init()
        assertEquals(State.SET_MANAGER, state, "init() should be called after setManager()")
        state = State.INIT
    }

    override fun start(frame: Frame) {
        super.start(frame)
        assertEquals(State.INIT, state, "start() should be called after init()")
        state = State.START
    }

    override fun update(frame: Frame) {
        super.update(frame)
        assertTrue(state == State.START || state == State.UPDATE, "update() should be called after start() or update()")
        state = State.UPDATE

        n--;
        if (n == 0) {
            done = true;
        }
    }

    override fun cleanup() {
        super.cleanup()
        assertTrue(state == State.START || state == State.UPDATE, "cleanup() should be called after start() or update()")
        state = State.CLEANUP
    }
}