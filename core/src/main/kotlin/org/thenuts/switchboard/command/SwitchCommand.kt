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
                cmd.start(frame)
                if (cmd.done) {
                    close(cmd)
                    done = true
                }
                return
            }
        }
        cmd = Command.NOP
        done = true
    }

    override fun update(frame: Frame) {
        if (cmd.done) {
            close(cmd)
            done = true
            return
        }

        cmd.update(frame)

        if (cmd.done) {
            close(cmd)
            done = true
        }
    }

    override fun cleanup() {
        if (!done) {
            close(cmd)
            done = true
        }
    }

    data class Case<T>(val pred: (T) -> Boolean, val command: Command)
}