package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class AwaitCommand(val predicate: (Frame) -> Boolean) : Command {
    override var done: Boolean = false

    override fun update(frame: Frame) {
        if (!done && predicate(frame))
            done = true
    }
}