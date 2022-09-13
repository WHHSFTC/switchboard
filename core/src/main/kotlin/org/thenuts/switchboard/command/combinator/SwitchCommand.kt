package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

class SwitchCommand<T>(val supplier: () -> T, val cases: List<Case<T>>) : Command {
    override var done: Boolean = false
    private lateinit var cmd: Command

    override fun start(frame: Frame) {
        val v = supplier()
        for (c in cases) {
            if (c.pred(v)) {
                cmd = c.command
                cmd.start(frame)
                done = cmd.done
                return
            }
        }
        cmd = Command.NOP
        done = cmd.done
    }

    override fun update(frame: Frame) {
        cmd.update(frame)
        done = cmd.done
    }

    override fun cleanup() {
        cmd.cleanup()
    }

    data class Case<T>(val pred: (T) -> Boolean, val command: Command)
}