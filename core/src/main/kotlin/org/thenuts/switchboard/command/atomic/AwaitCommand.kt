package org.thenuts.switchboard.command.atomic

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

/**
 * Command that calls a [predicate] each loop cycle until it is true, setting [done] to true.
 */
class AwaitCommand(val predicate: (Frame) -> Boolean) : Command {
    override var done: Boolean = false

    override fun update(frame: Frame) {
        if (!done && predicate(frame))
            done = true
    }

}