package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.store.ResourceHandler
import org.thenuts.switchboard.util.Frame

class SequentialCommand(val list: List<Command>) : Command {
    private var i = 0

    override var done: Boolean = false
        private set

    override var dependencies: Map<Any, ResourceHandler<*>> = mapOf()
        private set

    override fun start(frame: Frame) {
        startUntilNotDone(frame)
    }

    // run one update and, if done, cleanup then run as many starts as possible
    override fun update(frame: Frame) {
        list[i].update(frame)

        if (!list[i].done) return

        list[i].cleanup()
        i++

        startUntilNotDone(frame)
    }

    override fun cleanup() {
        if (i < list.size) {
            list[i].cleanup()
        }
    }

    private fun startUntilNotDone(frame: Frame) {
        while (true) {
            if (i >= list.size) {
                done = true
                return
            }

            list[i].start(frame)
            dependencies = list[i].dependencies

            if (!list[i].done)
                return

            list[i].cleanup()
            i++
        }
    }
}