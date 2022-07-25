package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class LinearCommand(val list: List<Command>) : CommandAbstract(), CommandManager {
    var i = 0
    var fresh = true

    override var done: Boolean = false

    override fun load() {
        if (list.isEmpty()) {
            done = true
            return
        }
        list[0].load()
    }

    override fun update(frame: Frame) {
        list[i].update(frame)
        if (list[i].done)
            list[i].cleanup()
        i++

        if (i >= list.size) {
            done = true
            return
        }

        list[i].load()
    }

    override fun cleanup() {
        if (i < list.size) {
            list[i].cleanup()
        }
    }

    override fun handleRegisterPrerequisite(src: Command, prereq: Command) {
        registerPrequisite(prereq)
    }

    override fun handleRegisterPostrequisite(src: Command, postreq: Command) {
        registerPostrequisite(postreq)
    }

    override fun handleDeregisterPrerequisite(src: Command, prereq: Command) {
        deregisterPrequisite(prereq)
    }

    override fun handleDeregisterPostrequisite(src: Command, postreq: Command) {
        deregisterPostrequisite(postreq)
    }
}