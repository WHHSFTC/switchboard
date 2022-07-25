package org.thenuts.switchboard.dsl

import org.thenuts.switchboard.command.*
import org.thenuts.switchboard.core.Frame
import org.thenuts.switchboard.scheduler.surelyList
import kotlin.time.Duration

class CommandListContext {
    private val list: MutableList<Command> = mutableListOf()

    fun <T> switch(supplier: () -> T, block: SwitchCommandContext<T>.() -> Unit) {
        list += makeSwitch(supplier, block)
    }

    fun await(predicate: (Frame) -> Boolean) {
        list += AwaitCommand(predicate)
    }

    fun awaitAll(vararg predicate: (Frame) -> Boolean) {
        list += ConcurrentCommand(predicate.surelyList().map {AwaitCommand(it)}, awaitAll = true)
    }

    fun race(vararg predicate: (Frame) -> Boolean) {
        list += ConcurrentCommand(predicate.surelyList().map {AwaitCommand(it)}, awaitAll = false)
    }

    fun awaitUntil(timeout: Duration, predicate: (Frame) -> Boolean) {
        this.concurrent(awaitAll = false) {
            await(predicate)
            delay(timeout)
        }
    }

    fun delay(duration: Duration) {
        list += DelayCommand(duration)
    }

    fun times(n: Int, b: CommandListContext.() -> Unit) {
        linear { repeat(n) { linear(b) } }
    }

    fun task(f: (Frame) -> Unit) {
        list += SimpleCommand(f)
    }

    fun sub(c: Command) {
        list += c
    }

    operator fun Command.unaryPlus() = sub(this)

    fun linear(b: CommandListContext.() -> Unit) {
        list += mkLinear(b)
    }

    fun concurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit) {
        list += mkConcurrent(awaitAll, b)
    }

    fun loop(pred: (Frame) -> Boolean, interrupt: Boolean = false, b: CommandListContext.() -> Unit) {
        list += mkLoop(pred, interrupt, b)
    }

    fun until(pred: (Frame) -> Boolean, interrupt: Boolean = false, b: CommandListContext.() -> Unit) {
        loop({ !pred(it) }, interrupt, b)
    }

    fun forever(b: CommandListContext.() -> Unit) {
        loop({ true }, interrupt = false, b)
    }

    fun build() = list
}

fun mkLinear(b: CommandListContext.() -> Unit)
        = LinearCommand(CommandListContext().apply(b).build())

fun mkConcurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit)
        = ConcurrentCommand(CommandListContext().apply(b).build(), awaitAll)

fun mkLoop(pred: (Frame) -> Boolean, interrupt: Boolean = false, b: CommandListContext.() -> Unit)
        = LoopCommand(pred, interrupt) { mkLinear(b) }

