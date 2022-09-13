package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

// TODO refactor command interface to reuse commands without real time generation
class LoopCommand(val pred: (Frame) -> Boolean, val interrupt: Boolean = false, val commandBuilder: () -> Command) : Command {
    private var cmd: Command? = null
    override var done: Boolean = false

    override fun start(frame: Frame) {
        cmd = commandBuilder().also {
            it.start(frame)
        }
    }

    override fun update(frame: Frame) {
        if ((interrupt || cmd == null) && pred(frame)) {
            cmd?.let {
                it.cleanup()
            }
            cmd = null
            done = true
            return
        }

        val c = cmd ?: commandBuilder().also {
            it.start(frame)
        }

        c.update(frame)

        if (c.done) {
            c.cleanup()
            cmd = null
        }
    }

    override fun cleanup() {
        cmd?.let {
            it.cleanup()
        }
    }
}