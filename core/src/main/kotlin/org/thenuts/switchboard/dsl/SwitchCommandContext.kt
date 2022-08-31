package org.thenuts.switchboard.dsl

import org.thenuts.switchboard.command.combinator.*

@CommandDsl
class SwitchCommandContext<T>(val strict: Boolean) {
    private val list: MutableList<SwitchCommand.Case<T>> = mutableListOf()

    /**
     * Creates a branch that will match if `predicate(supplier())` is `true`.
     */
    fun satisfies(predicate: (T) -> Boolean, block: CommandListContext.() -> Unit) {
        list += SwitchCommand.Case<T>(predicate, mkLinear(strict, block))
    }

    /**
     * Creates a branch that will match if `supplier() == caseSupplier()`.
     */
    fun matches(caseSupplier: () -> T, block: CommandListContext.() -> Unit) {
        satisfies({ it == caseSupplier()}, block)
    }

    /**
     * Creates a branch that will match if `supplier() == v`.
     */
    fun value(v: T, block: CommandListContext.() -> Unit) {
        matches({v}, block)
    }

    /**
     * Creates a branch that will match unconditionally.
     */
    fun fallback(block: CommandListContext.() -> Unit) {
        satisfies({ true }, block)
    }

    fun build() = list
}

/**
 * Constructs a [SwitchCommand] that will evaluate one branch from [block] based on [supplier]. Only
 * the first branch to match will be evaluated. If no branches match, the command acts as a no-op.
 *
 * @param strict Whether to place [StrictCommand] wrappers to catch lifecycle bugs.
 * @param supplier Function to be called at runtime to pick a branch.
 * @param block Block to populate a list of branches using the SwitchCommand DSL.
 */
fun <T> mkSwitch(strict: Boolean, supplier: () -> T, block: SwitchCommandContext<T>.() -> Unit)
        = SwitchCommand<T>(supplier, SwitchCommandContext<T>(strict).apply(block).build())

fun SwitchCommandContext<Boolean>.then(b: CommandListContext.() -> Unit) = this.value(true, b)
