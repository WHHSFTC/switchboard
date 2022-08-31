package org.thenuts.switchboard.dsl

import org.thenuts.switchboard.command.*
import org.thenuts.switchboard.command.atomic.AwaitCommand
import org.thenuts.switchboard.command.atomic.DelayCommand
import org.thenuts.switchboard.command.atomic.SimpleCommand
import org.thenuts.switchboard.command.combinator.*
import org.thenuts.switchboard.util.Frame
import kotlin.time.Duration

@CommandDsl
class CommandListContext(val strict: Boolean) {
    private val list: MutableList<Command> = mutableListOf()

    /**
     * Adds a [Command] to the list.
     */
    fun add(cmd: Command) {
        list += if (strict) StrictCommand(cmd) else cmd
    }

    /**
     * Adds a [SwitchCommand] that chooses which branch to execute based on the [supplier].
     */
    fun <T> switch(supplier: () -> T, block: SwitchCommandContext<T>.() -> Unit) {
        add(mkSwitch(strict, supplier, block))
    }

    /**
     * Adds an [AwaitCommand] that is done when the [predicate] returns true.
     */
    fun await(predicate: (Frame) -> Boolean) {
        add(AwaitCommand(predicate))
    }

    /**
     * Adds a [ConcurrentCommand] that is done when all of the predicates have returned true.
     */
    fun awaitAll(vararg predicate: (Frame) -> Boolean) {
        add(ConcurrentCommand(predicate.asList().map { AwaitCommand(it) }, awaitAll = true))
    }

    /**
     * Adds a [ConcurrentCommand] that is done when any of the predicates returns true.
     */
    fun awaitAny(vararg predicate: (Frame) -> Boolean) {
        add(ConcurrentCommand(predicate.asList().map { AwaitCommand(it) }, awaitAll = false))
    }

    /**
     * Adds a [ConcurrentCommand] containing an that is done when the [predicate] returns true or
     * when the [timeout] is up, whichever comes first.
     */
    fun awaitUntil(timeout: Duration, predicate: (Frame) -> Boolean) {
        this.concurrent(awaitAll = false) {
            await(predicate)
            delay(timeout)
        }
    }

    /**
     * Adds a [DelayCommand] that is done when the [duration] is up.
     */
    fun delay(duration: Duration) {
        add(DelayCommand(duration))
    }

    /**
     * Adds a [LinearCommand] containing [n] generated commands from the [block].
     */
    fun times(n: Int, block: CommandListContext.() -> Unit) {
        linear { repeat(n) { linear(block) } }
    }

    /**
     * Adds a [SimpleCommand] that only calls the function [f].
     */
    fun task(f: (Frame) -> Unit) {
        add(SimpleCommand(f))
    }

    /**
     * Adds a [LinearCommand] that runs the list of commands generated by [block] one by one.
     */
    fun linear(block: CommandListContext.() -> Unit) {
        add(mkLinear(strict, block))
    }

    /**
     * Adds a [ConcurrentCommand] that runs the list of commands generated by [block] simultaneously.
     *
     * @param awaitAll If `true`, wait for all subcommands to finish; if `false`, interrupt
     * remaining subcommands after any one finishes.
     */
    fun concurrent(awaitAll: Boolean = true, block: CommandListContext.() -> Unit) {
        add(mkConcurrent(strict, awaitAll, block))
    }

    /**
     * Adds a [LoopCommand] that generates and runs commands from [block] **at runtime** while
     * [predicate] returns true. If [interrupt] is true, it will check every update, instead of at
     * the top of every longer loop iteration.
     *
     * Runtime generation of commands has the potential to be very slow. Moving slow operations such
     * as trajectory generation out of [block] is recommended. If there is a reasonable upper bound
     * to the number of iterations, consider using [times] to generate each iteration beforehand.
     */
    fun loop(predicate: (Frame) -> Boolean, interrupt: Boolean = false, block: CommandListContext.() -> Unit) {
        add(mkLoop(strict, predicate, interrupt, block))
    }

    /**
     * Adds a [LoopCommand] that generates and runs commands from [block] **at runtime** while
     * [predicate] returns false. If [interrupt] is true, it will check every update, instead of at
     * the top of every longer loop iteration.
     *
     * See [loop] for important performance warnings.
     */
    fun until(predicate: (Frame) -> Boolean, interrupt: Boolean = false, block: CommandListContext.() -> Unit) {
        loop({ !predicate(it) }, interrupt, block)
    }

    /**
     * Adds a [LoopCommand] that generates and runs commands from [block] **at runtime** forever.
     *
     * See [loop] for important performance warnings.
     */
    fun forever(block: CommandListContext.() -> Unit) {
        loop({ true }, interrupt = false, block)
    }

    internal fun build() = list
}

/**
 * Constructs a [LinearCommand] that will evaluate a list of commands from [block] one by one.
 *
 * @param strict Whether to place [StrictCommand] wrappers to catch lifecycle bugs.
 * @param block Block to populate a list of commands using the Command DSL.
 */
fun mkLinear(strict: Boolean = false, block: CommandListContext.() -> Unit): Command
        = LinearCommand(CommandListContext(strict).apply(block).build())

/**
 * Constructs a [ConcurrentCommand] that will evaluate a list of commands from [block]
 * simultaneously.
 *
 * @param strict Whether to place [StrictCommand] wrappers to catch lifecycle bugs.
 * @param awaitAll If `true`, wait for all subcommands to finish; if `false`, interrupt
 * remaining subcommands after any one finishes.
 * @param block Block to populate a list of commands using the Command DSL.
 */
fun mkConcurrent(strict: Boolean = false, awaitAll: Boolean = true, block: CommandListContext.() -> Unit): Command
        = ConcurrentCommand(CommandListContext(strict).apply(block).build(), awaitAll)

/**
 * Constructs a [LoopCommand] that generates and runs commands from [block] **at runtime** forever.
 *
 * @param strict Whether to place [StrictCommand] wrappers to catch lifecycle bugs.
 * @param predicate When this returns false, the loop is ended.
 * @param interrupt If `true`, check [predicate] on every update; if `false`, check [predicate] only
 * at top of each longer loop cycle.
 * @param block Block to populate a list of commands using the Command DSL.
 */
fun mkLoop(strict: Boolean = false, predicate: (Frame) -> Boolean, interrupt: Boolean = false, block: CommandListContext.() -> Unit): Command
        = LoopCommand(predicate, interrupt) { mkLinear(strict, block) }
