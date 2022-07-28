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
                cmd.setManager(this)
                cmd.init()
                cmd.start(frame)
                return
            }
        }
        cmd = Command.NOP
    }

    override fun update(frame: Frame) {
        cmd.update(frame)
        if (cmd.done) {
            done = true
            cmd.cleanup()
            deregisterAll()
        }
    }

    override fun cleanup() {
        if (!done) {
            cmd.cleanup()
            deregisterAll()
        }
    }

    data class Case<T>(val pred: (T) -> Boolean, val command: Command)
}