package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class ConcurrentCommand(val list: List<Command>, val awaitAll: Boolean = true) : CommandAbstract(), CommandManager {
    var mut = list.toMutableList()

    override var done: Boolean = false

    override fun init() {
        list.forEach { it.setManager(this) }
        list.forEach { it.init() }
    }

    override fun start(frame: Frame) {
        mut = list.toMutableList()
        mut.forEach { it.start(frame) }
    }

    override fun update(frame: Frame) {
        mut.removeAll { cmd ->
            if (cmd.done) {
                cmd.cleanup()
                true
            } else false
        }

        if (mut.isEmpty() || (!awaitAll && mut.size != list.size)) {
            mut.forEach { it.cleanup() }
            done = true
        } else {
            mut.forEach { it.update(frame) }
        }
    }

    override fun cleanup() {
        mut.forEach { it.cleanup() }
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