package org.thenuts.switchboard.dsl

import org.thenuts.switchboard.command.*

class SwitchCommandContext<T> {
    private val list: MutableList<SwitchCommand.Case<T>> = mutableListOf()

    fun satisfies(predicate: (T) -> Boolean, b: CommandListContext.() -> Unit) {
        list += SwitchCommand.Case<T>(predicate, mkLinear(b))
    }

    fun matches(supplier: () -> T, b: CommandListContext.() -> Unit) {
        satisfies({ it == supplier()}, b)
    }

    fun value(v: T, b: CommandListContext.() -> Unit) {
        matches({v}, b)
    }

    fun fallback(b: CommandListContext.() -> Unit) {
        satisfies({ true }, b)
    }

    fun build() = list
}

fun <T> makeSwitch(supplier: () -> T, b: SwitchCommandContext<T>.() -> Unit)
        = SwitchCommand<T>(supplier, SwitchCommandContext<T>().apply(b).build())

fun SwitchCommandContext<Boolean>.then(b: CommandListContext.() -> Unit) = this.value(true, b)
