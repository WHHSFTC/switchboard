package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class ConcurrentCommand(val list: List<Command>, val awaitAll: Boolean = true) : Combinator() {
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
        if (mut.isEmpty() || (!awaitAll && mut.size != list.size)) {
            mut.forEach { it.cleanup(); commandManager.handleDeregisterAll(it) }
            mut.clear()
            done = true
        } else {
            mut.forEach { it.update(frame) }
        }

        mut.removeAll { cmd ->
            if (cmd.done) {
                cmd.cleanup()
                commandManager.handleDeregisterAll(cmd)
                true
            } else false
        }
    }

    override fun cleanup() {
        mut.forEach {
            it.cleanup()
            handleDeregisterAll(it)
        }
    }
}