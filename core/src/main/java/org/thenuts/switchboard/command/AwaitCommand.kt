package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class AwaitCommand(val predicate: (Frame) -> Boolean) : CommandAbstract() {
    override fun load() { }

    override fun update(frame: Frame) {
        if (!done && predicate(frame))
            done = true
    }

}