package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

// TODO refactor command interface to reuse commands without real time generation
class LoopCommand(val commandBuilder: (Frame) -> Command, val pred: () -> Boolean) : Command {
    private var cmd: Command? = null
    override var done: Boolean = false

    override fun load(frame: Frame) { }

    override fun update(frame: Frame) {
        if (this.done) return
        val c = cmd
        when {
            c == null -> {
                cmd = commandBuilder(frame)
            }
            c.done -> {
                if (pred()) {
                    this.done = true
                    return
                } else {
                    cmd = commandBuilder(frame).also { it.load(frame) }
                }
            }
            else -> {
                c.update(frame)
            }
        }
    }

    override fun cleanup() {
        cmd?.let { if (it.done) it.cleanup() }
    }
}