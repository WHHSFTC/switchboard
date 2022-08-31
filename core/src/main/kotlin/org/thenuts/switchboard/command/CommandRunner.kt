package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class CommandRunner(val cmd: Command, var state: State = State.PRE_START) {
    fun step(frame: Frame): Boolean {
        state = state.block(cmd, frame)
        return state == State.DELETION
    }

    fun interrupt() {
        if (state == State.PRE_UPDATE) {
            cmd.cleanup()
        }
        state = State.DELETION
    }

    enum class State(val block: (Command, Frame) -> State) {
        PRE_START({ cmd, frame ->
            cmd.start(frame)
            if (cmd.done) {
                cmd.cleanup()
                DELETION
            } else {
                cmd.update(frame)
                if (cmd.done) {
                    cmd.cleanup()
                    DELETION
                } else {
                    PRE_UPDATE
                }
            }
        }),
        PRE_UPDATE({ cmd, frame ->
            cmd.update(frame)
            if (cmd.done) {
                cmd.cleanup()
                DELETION
            } else {
                PRE_UPDATE
            }
        }),
        DELETION({ _, _ ->
            DELETION
        })
    }
}

