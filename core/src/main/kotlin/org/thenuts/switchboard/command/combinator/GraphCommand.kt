package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.CommandScheduler
import org.thenuts.switchboard.util.Frame

/**
 * Like [ParallelCommand], but orders subcommands using a [CommandScheduler].
 */
class GraphCommand(val list: List<Command>, val awaitAll: Boolean = false, strict: Boolean = false) : Combinator() {
    val sched = CommandScheduler(strict)

    init {
        subCommands = list
        list.forEach { sched.addCommand(it) }
    }

    override var done: Boolean = false

    override fun update(frame: Frame) {
        sched.update(frame)
        done = sched.nodes.isEmpty() || !awaitAll && sched.nodes.size != list.size
    }

    override fun cleanup() {
        sched.clear()
    }
}