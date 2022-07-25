package org.thenuts.switchboard.core

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.CommandListContext
import org.thenuts.switchboard.command.LinearCommand
import org.thenuts.switchboard.scheduler.HardwareScheduler
import org.thenuts.switchboard.structures.FullRingBuffer
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

abstract class Robot(val logger: Logger, val config: Configuration, val name: String) {
    protected abstract val activities: List<Command>
    private val commands: MutableList<Command> = mutableListOf()
    private val insertions: MutableList<Command> = mutableListOf()
    private val deletions: MutableList<Command> = mutableListOf()
    protected abstract val scheduler: HardwareScheduler
    protected var frame = Frame(0, 0.nanoseconds, 1.milliseconds)
    private val ring: FullRingBuffer<Long> = FullRingBuffer(SAMPLE) { frame.runtime.nanoseconds }
    var meanCycle: Long = 0
        private set
    var frequency: Double = 0.0
        private set

    private fun appendCommand(a: Command): Boolean {
        if (a in commands)
            return false
        commands.add(a)
        a.load(frame)
        return true
    }

    private fun prependCommand(a: Command): Boolean {
        if (a in commands)
            return false
        commands.add(0, a)
        a.load(frame)
        return true
    }

    private fun insertCommand(a: Command): Boolean {
        if (a in commands)
            return false
        commands.add(a)
        return false
        val pres = commands.mapIndexedNotNull { i, c -> if (c in a.prereqs) i to c else null }
        val posts = commands.mapIndexedNotNull { i, c -> if (c in a.postreqs) i to c else null }
        val lastPre: Int = pres.maxByOrNull { (i, _) -> i }?.first ?: -1
        val firstPost: Int = posts.minByOrNull { (i, _) -> i }?.first ?: commands.size
        if (lastPre < firstPost || !STRICT) {
            commands.add(lastPre + 1, a)
            return true
        } else {
            logger.addMessage("cycle error", 30.seconds)
            return false
        }
    }

    fun forceDeleteCommands(predicate: (Command) -> Boolean): Boolean {
        var any = false
        commands.forEach {
            if (predicate(it) && it !in deletions) {
                deletions.add(it)
                any = true
            }
        }
        return any
    }

    fun queueCommand(a: Command): Boolean {
        if (a in commands)
            return false
        insertions.add(a)
        return true
    }

    fun softDeleteCommands(predicate: (Command) -> Boolean): Boolean {
        var any = false
        commands.forEach {
            if (predicate(it) && !it.done) {
                it.cleanup()
                if (it !in deletions)
                    deletions.add(it)
                any = true
            }
        }
        return any
    }

    private fun removeCommand(cmd: Command): Boolean {
        return commands.remove(cmd)
    }

    private fun removeCommands(predicate: (Command) -> Boolean): Boolean {
        return commands.removeAll(predicate)
    }

    fun setup() {
        logger.out["expected cycle (ms)"] = scheduler.getWorstMean().milliseconds
        activities.forEach { insertCommand(it) }
        commands.forEach { it.load(frame) }
        scheduler.output(all = true)
        logger.update()
    }

    open fun update() {
        frame = Frame.from(frame)
        logger.err["frame"] = frame

        val oldTime = ring.read(0)
        ring.write(frame.runtime.inWholeNanoseconds)
        val steps = (ring.list.subList(0, ring.list.size - 1) zip ring.list.subList(1, ring.list.size)).map { (a, b) -> b - a }
        val maxCycle = steps.maxOrNull() ?: 0L
        val minCycle = steps.minOrNull() ?: 0L
        meanCycle = (frame.runtime.inWholeNanoseconds - oldTime) / SAMPLE
        frequency = if (meanCycle == 0L) Double.NaN else 1.seconds.inWholeNanoseconds.toDouble() / meanCycle.toDouble()
        logger.out["min cycle  (ms)"] = minCycle.nanoseconds.inWholeMilliseconds
        logger.out["mean cycle (ms)"] = meanCycle.nanoseconds.inWholeMilliseconds
        logger.out["max cycle  (ms)"] = maxCycle.nanoseconds.inWholeMilliseconds
        logger.out["frequency  (Hz)"] = frequency

        config.read()

        commands.forEach { it.update(frame) }

        logger.err["commands"] = commands.map { it.javaClass.canonicalName }
        logger.err["insertions"] = insertions.map { it.javaClass.canonicalName }

        insertions.forEach { it.load(frame); insertCommand(it) }
        insertions.clear()
        forceDeleteCommands { it.done }
        logger.err["deletions"] = deletions.map { it.javaClass.canonicalName }

        deletions.forEach { removeCommand(it) }
        deletions.clear()

        scheduler.output()

        logger.update()
    }

    fun cleanup() {
        commands.forEach { if (!it.done) it.cleanup() }
        scheduler.output(all = true)
        logger.update()
    }

    override fun toString(): String = name

    fun launch(b: CommandListContext.() -> Unit): Command {
        val cmd = LinearCommand(CommandListContext().apply(b).build())
        queueCommand(cmd)
        return cmd
    }

    class CyclicGraphException: Exception("Command graph contains cycles in STRICT mode:w")

    companion object {
        @JvmField var SAMPLE: Int = 100
        @JvmField var STRICT: Boolean = false
    }
}