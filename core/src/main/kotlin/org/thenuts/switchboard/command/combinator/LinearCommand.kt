package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

class LinearCommand(val list: List<Command>) : Combinator() {
    private var i = 0

    override var done: Boolean = false
        private set

    override fun start(frame: Frame) {
        startUntilNotDone(frame)
    }

    // run one update and, if done, cleanup then run as many starts as possible
    override fun update(frame: Frame) {
        list[i].update(frame)

        if (!list[i].done) return

        close(list[i])
        i++

        startUntilNotDone(frame)
    }

    override fun cleanup() {
        if (i < list.size) {
            close(list[i])
        }
    }

    private fun startUntilNotDone(frame: Frame) {
        while (true) {
            if (i >= list.size) {
                done = true
                return
            }

            setup(list[i], frame)

            if (!list[i].done)
                return

            close(list[i])
            i++
        }
    }
}