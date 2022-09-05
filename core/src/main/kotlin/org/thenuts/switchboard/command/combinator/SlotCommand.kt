package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.CommandRunner
import org.thenuts.switchboard.util.Frame

class SlotCommand(
    override val prereqs: List<Pair<Command, Int>>,
    override val postreqs: List<Pair<Command, Int>>,
) : Combinator() {
    override val done: Boolean = false

    private var runner: CommandRunner? = null
    private val queue: MutableList<Command> = mutableListOf()
    private var interruptFlag = false

    override fun update(frame: Frame) {
        if (interruptFlag) {
            runner?.interrupt()
            interruptFlag = false
        }

        // if done or null, queue next command
        if (runner?.step(frame) != false) {
            runner = queue.removeFirstOrNull()?.let { CommandRunner(it) }
        }
    }

    override fun cleanup() {
        runner?.interrupt()
    }

    fun queue(cmd: Command) {
        queue.add(cmd)
    }

    fun interrupt(cmd: Command?) {
        interruptFlag = true
        queue.clear()
        cmd?.let { queue.add(it) }
    }
}