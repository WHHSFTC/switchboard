package org.thenuts.switchboard.command

@Suppress("NAME_SHADOWING")
abstract class Combinator : CommandAbstract(), CommandManager {
    override fun handleRegisterEdge(owner: Command, before: Command, after: Command) {
        val before = if (owner == before) this else before
        val after = if (owner == after) this else after
        commandManager.handleRegisterEdge(owner, before, after)
    }

    override fun handleDeregisterEdge(owner: Command, before: Command, after: Command) {
        val before = if (owner == before) this else before
        val after = if (owner == after) this else after
        commandManager.handleDeregisterEdge(owner, before, after)
    }

    override fun handleDeregisterAll(src: Command) {
        commandManager.handleDeregisterAll(src)
    }
}