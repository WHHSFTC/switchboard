package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

class ParallelCommand(val list: List<Command>, val awaitAll: Boolean = true) : Command {
    val mut: MutableList<Command> = list.toMutableList()

    override var done: Boolean = false
        private set

    override fun start(frame: Frame) {
        mut.forEach {
            it.start(frame)
        }
        removeDone()
    }

    override fun update(frame: Frame) {
        mut.forEach {
            it.update(frame)
        }
        removeDone()
    }

    override fun cleanup() {
        mut.forEach {
            it.cleanup()
        }
    }

    private fun removeDone() {
        mut.removeAll { cmd ->
            if (cmd.done) {
                cmd.cleanup()
                true
            } else {
                false
            }
        }

        if (mut.isEmpty() || (!awaitAll && mut.size != list.size)) {
            mut.forEach {
                it.cleanup()
            }
            mut.clear()
            done = true
        }
    }
}