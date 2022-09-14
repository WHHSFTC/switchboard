package org.thenuts.switchboard.command.atomic

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

/**
 * Calls [lambda] on start, then completes.
 */
class SimpleCommand(val lambda: (Frame) -> Unit) : Command {
    override var done: Boolean = false

    override fun start(frame: Frame) {
        lambda(frame)
        done = true
    }
}