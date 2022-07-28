package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class LinearCommand(val list: List<Command>) : Combinator() {
    private var i = 0

    override var done: Boolean = false

    override fun start(frame: Frame) {
        if (list.isEmpty()) {
            done = true
            return
        }
        list[0].setManager(this)
        list[0].init()
        list[0].start(frame)
    }

    override fun update(frame: Frame) {
        do {
            list[i].update(frame)

            if (!list[i].done) return

            list[i].cleanup()
            handleDeregisterAll(list[i])
            i++

            if (i >= list.size) {
                done = true
                return
            }

            list[i].setManager(this)
            list[i].init()
            list[i].start(frame)
        } while (list[i].done)
    }

    override fun cleanup() {
        if (i < list.size) {
            list[i].cleanup()
            handleDeregisterAll(list[i])
        }
    }
}