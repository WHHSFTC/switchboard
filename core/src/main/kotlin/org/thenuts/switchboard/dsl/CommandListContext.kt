package org.thenuts.switchboard.dsl

import org.thenuts.switchboard.command.*
import org.thenuts.switchboard.util.Frame
import kotlin.time.Duration

@SwitchboardDsl
class CommandListContext {
    private val list: MutableList<CommandSupplier> = mutableListOf()

    fun add(supplier: CommandSupplier) {
        list += supplier
    }

    fun <T> switch(supplier: () -> T, block: SwitchCommandContext<T>.() -> Unit) {
        add(mkSwitch(supplier, block))
    }

    fun await(predicate: (Frame) -> Boolean) {
        add { AwaitCommand(predicate) }
    }

    fun awaitUntil(timeout: Duration, predicate: (Frame) -> Boolean) {
        concurrent(awaitAll = false) {
            await(predicate)
            delay(timeout)
        }
    }

    fun delay(duration: Duration) {
        add { DelayCommand(duration) }
    }

    fun times(n: Int, b: CommandListContext.() -> Unit) {
        linear { repeat(n) { linear(b) } }
    }

    fun task(f: (Frame) -> Unit) {
        add { SimpleCommand(f) }
    }

    fun linear(b: CommandListContext.() -> Unit) {
        add(mkLinear(b))
    }

    fun concurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit) {
        add(mkConcurrent(awaitAll, b))
    }

    fun loop(b: CommandListContext.() -> Unit) {
        add(mkLoop(b))
    }

    fun build() = list
}

fun mkLinear(b: CommandListContext.() -> Unit): CommandSupplier {
    val list = CommandListContext().apply(b).build()
    val seq = Sequence { list.iterator() }
    return { SequentialCommand(Sequence { list.iterator() }, pregen = true) }
}

fun mkConcurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit): CommandSupplier {
    val list = CommandListContext().apply(b).build()
    return { ConcurrentCommand(list, awaitAll) }
}

fun mkLoop(b: CommandListContext.() -> Unit): CommandSupplier {
    val list = CommandListContext().apply(b).build()
    val inner = Sequence { list.iterator() }
    val outer = generateSequence<CommandSupplier> { { SequentialCommand(inner, pregen = true) } }
    return { SequentialCommand(outer, pregen = false) }
}

