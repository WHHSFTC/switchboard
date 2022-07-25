package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class SimpleCommand(val lambda: (Frame) -> Unit) : Command {
    override var done: Boolean = false

    override fun start(frame: Frame) {
        lambda(frame)
        done = true
    }
}