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

    fun times(n: Int, initAll: Boolean = false, b: CommandListContext.() -> Unit) {
        linear { repeat(n) { linear(initAll, b) } }
    }

    fun task(f: (Frame) -> Unit) {
        add { SimpleCommand(f) }
    }

    fun linear(initAll: Boolean = false, b: CommandListContext.() -> Unit) {
        add(mkLinear(initAll, b))
    }

    fun concurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit) {
        add(mkConcurrent(awaitAll, b))
    }

    fun loop(b: CommandListContext.() -> Unit) {
        add(mkLoop(b))
    }

    fun build() = list
}

fun mkLinear(initAll: Boolean = false, b: CommandListContext.() -> Unit): CommandSupplier {
    val list = CommandListContext().apply(b).build()
    val seq = Sequence { list.iterator() }
    return {
        val iter = list.iterator()
        SequentialCommand(Sequence { iter }, initAll)
    }
}

fun mkConcurrent(awaitAll: Boolean = true, b: CommandListContext.() -> Unit): CommandSupplier {
    val list = CommandListContext().apply(b).build()
    return {
        ConcurrentCommand(list, awaitAll)
    }
}

fun mkLoop(initAll: Boolean = false, b: CommandListContext.() -> Unit): CommandSupplier {
    val list = CommandListContext().apply(b).build()
    return {
        val outer = generateSequence<Command> {
            val iter = list.iterator()
            SequentialCommand(Sequence { iter }, initAll)
        }
        SequentialCommand(outer, initAll = false)
    }
}
