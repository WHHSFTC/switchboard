package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.store.ResourceHandler
import org.thenuts.switchboard.util.Frame

/**
 * Wraps a Command and makes assertions about the order of method calls.
 */
class StrictCommand(val cmd: Command) : Combinator() {
    init {
        subCommands = listOf(cmd)
    }

    private var doneCheckAllowed = false

    enum class State(val stable: Boolean = false) {
        PRE_START(true),
        POST_START,
        POST_UPDATE,
        POST_CLEANUP(true)
    }

    private var state: State = State.PRE_START

    override val done: Boolean
        get() {
            assert(doneCheckAllowed) { "done must be checked exactly once after every start and update" }
            doneCheckAllowed = false
            return cmd.done
        }

    override fun start(frame: Frame) {
        assert(state == State.PRE_START) { "start must be called first" }
        cmd.start(frame)
        doneCheckAllowed = true
        state = State.POST_START
    }

    override fun update(frame: Frame) {
        assert(state == State.POST_START || state == State.POST_UPDATE) { "update must be called after start or update" }
        assert(!doneCheckAllowed) { "done must be checked after every start and update" }
        cmd.update(frame)
        doneCheckAllowed = true
        state = State.POST_UPDATE
    }

    override fun cleanup() {
        assert(state == State.POST_START || state == State.POST_UPDATE) { "cleanup must be called after start or update" }
        doneCheckAllowed = false
        state = State.POST_CLEANUP
    }
}