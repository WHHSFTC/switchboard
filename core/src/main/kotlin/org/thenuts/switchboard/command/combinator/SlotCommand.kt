package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.CommandRunner
import org.thenuts.switchboard.util.Frame

/**
 * Runs a [default] Command, switching to other Commands by the [interrupt] or [queue] methods.
 *
 * This is useful for, for example, running manual or empty controls by default and switching to
 * automated movements during tele.
 *
 * @param default Default Command to run when no others are active. Can be [Command.IDLE] for empty.
 * @param prereqs Extra prerequisites to declare, in addition to those of the default.
 * @param postreqs Extra postrequisites to declare, in addition to those of the default.
 */
class SlotCommand(
    val default: Command = Command.IDLE,
    prereqs: List<Pair<Command, Int>> = listOf(),
    postreqs: List<Pair<Command, Int>> = listOf(),
) : Command {
    override var done: Boolean = false

    override val prereqs: List<Pair<Command, Int>> = (prereqs union default.prereqs).toList()
    override val postreqs: List<Pair<Command, Int>> = (postreqs union default.postreqs).toList()

    private var defaultRunner = CommandRunner(default)
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

        // if still null (queue empty), step default
        if (runner == null) {
            done = defaultRunner.step(frame)
        }
    }

    override fun cleanup() {
        runner?.interrupt()
        defaultRunner.interrupt()
    }

    /**
     * Adds a Command to the queue, meaning that this Command will be run when all currently queued
     * are done.
     * @param cmd Command to schedule.
     */
    fun queue(cmd: Command) {
        queue.add(cmd)
    }

    /**
     * Clears the queue and places this in the first position. On the next update, the current
     * Command will be interrupted and this will take its place.
     * @param cmd Command to schedule, or null for return to default.
     */
    fun interrupt(cmd: Command?) {
        interruptFlag = true
        queue.clear()
        cmd?.let { queue.add(it) }
    }
}