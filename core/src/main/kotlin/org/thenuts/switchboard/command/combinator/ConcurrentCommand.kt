package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

class ConcurrentCommand(val list: List<Command>, val awaitAll: Boolean = true) : Combinator() {
    val mut: MutableList<Command> = list.toMutableList()

    override var done: Boolean = false
        private set

    override fun start(frame: Frame) {
        mut.forEach {
            setup(it, frame)
        }
        removeDone()
    }

    override fun update(frame: Frame) {
        mut.forEach { it.update(frame) }

        removeDone()

        if (mut.isEmpty() || (!awaitAll && mut.size != list.size)) {
            mut.forEach { close(it) }
            mut.clear()
            done = true
        }
    }

    override fun cleanup() {
        mut.forEach {
            close(it)
        }
    }

    private fun removeDone() {
        mut.removeAll { cmd ->
            if (cmd.done) {
                close(cmd)
                true
            } else {
                false
            }
        }
    }
}