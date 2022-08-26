package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class SwitchCommand<T>(val supplier: () -> T, val cases: List<Case<T>>) : Combinator() {
    override var done: Boolean = false
    private lateinit var cmd: Command

    override fun start(frame: Frame) {
        val v = supplier()
        for (c in cases) {
            if (c.pred(v)) {
                cmd = c.command
                setup(cmd, frame)
                return
            }
        }
        cmd = Command.NOP
    }

    override fun update(frame: Frame) {
        cmd.update(frame)
        done = cmd.done
    }

    override fun cleanup() {
        close(cmd)
    }

    data class Case<T>(val pred: (T) -> Boolean, val command: Command)
}