package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class StrictCommand(val cmd: Command) : Combinator() {
    private var doneCheckAllowed = false

    enum class State(val stable: Boolean = false) {
        PRE_INIT(true),
        POST_INIT,
        POST_START,
        POST_UPDATE,
        POST_CLEANUP(true)
    }

    private var state: State = State.PRE_INIT

    override val done: Boolean
        get() {
            assert(doneCheckAllowed) { "done must be checked exactly once after every start and update" }
            doneCheckAllowed = false
            return cmd.done
        }

    override fun init(manager: CommandManager) {
        assert(state == State.PRE_INIT) { "init must be called first" }
        super.init(manager)
        cmd.init(this)
        state = State.POST_INIT
    }

    override fun start(frame: Frame) {
        assert(state == State.POST_INIT) { "start must be called after init" }
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