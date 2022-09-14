package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

/**
 * Executes all of the Commands in [list] simultaneously.
 *
 * @param awaitAll If true, finishes when all subcommands are done. Otherwise, finishes when the
 * first finishes.
 */
class ParallelCommand(val list: List<Command>, val awaitAll: Boolean = true) : Combinator() {
    val mut: MutableList<Command> = list.toMutableList()

    init {
        subCommands = list
    }

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