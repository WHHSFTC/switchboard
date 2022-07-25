package org.thenuts.switchboard.dsl

import org.thenuts.switchboard.core.Frame
import org.thenuts.switchboard.scheduler.surelyList
import org.thenuts.switchboard.units.Time

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

    fun awaitAny(vararg predicate: (Frame) -> Boolean) {
        list += ConcurrentCommand(predicate.surelyList().map {AwaitCommand(it)}, awaitAll = false)
    }

    fun awaitUntil(millis: Long, predicate: (Frame) -> Boolean) {
        this.awaitUntil(Time.milli(millis), predicate)
    }

    fun awaitUntil(timeout: Time, predicate: (Frame) -> Boolean) {
        this.concurrent(awaitAll = false) {
            await(predicate)
            delay(timeout)
        }
    }

    fun delay(duration: Time) {
        list += DelayCommand(duration)
    }

    fun delay(millis: Long) {
        this.delay(Time.milli(millis))
    }

    fun times(n: Int, b: CommandListContext.() -> Unit) {
        linear { repeat(n) { linear(b) } }
    }

    // TODO refactor command interface to reuse commands without real time generation
    fun until(pred: (Frame) -> Boolean, b: CommandListContext.() -> Unit) {
        concurrent(awaitAll = false) {
            forever(b)
            await(pred)
        }
    }

    fun forever(b: CommandListContext.() -> Unit) {
        times(20, b)
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

    fun build() = list
}

fun mkLinear(b: CommandListContext.() -> Unit)
        = LinearCommand(CommandListContext().apply(b).build())

fun mkConcurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit)
        = ConcurrentCommand(CommandListContext().apply(b).build(), awaitAll)
