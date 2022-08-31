package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class CommandRunner(val cmd: Command, var state: State = State.PRE_START) {
    private fun singleStep(frame: Frame) {
        state = state.block(cmd, frame)
    }

    fun step(frame: Frame): Boolean {
        singleStep(frame)
        if (state == State.PRE_CLEANUP) singleStep(frame)
        return state == State.DELETION
    }

    fun interrupt() {
        if (state == State.PRE_UPDATE || state == State.PRE_CLEANUP) {
            cmd.cleanup()
        }
        state = State.DELETION
    }

    enum class State(val block: (Command, Frame) -> State) {
        PRE_START({ cmd, frame ->
            cmd.start(frame)
            if (cmd.done) PRE_CLEANUP
            else PRE_UPDATE
        }),
        PRE_UPDATE({ cmd, frame ->
            cmd.update(frame)
            if (cmd.done) PRE_CLEANUP
            else PRE_UPDATE
        }),
        PRE_CLEANUP({ cmd, _ ->
            cmd.cleanup()
            DELETION
        }),
        DELETION({ _, _ ->
            DELETION
        })
    }
}

