package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

// TODO refactor command interface to reuse commands without real time generation
class LoopCommand(val pred: (Frame) -> Boolean, val interrupt: Boolean = false, val commandBuilder: () -> Command) : Combinator() {
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
                close(it)
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
            close(c)
            cmd = null
        }
    }

    override fun cleanup() {
        cmd?.let {
            close(it)
        }
    }
}