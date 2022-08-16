package org.thenuts.switchboard.command

context(CommandContext)
@Suppress("NAME_SHADOWING")
abstract class Combinator : CommandAbstract(), CommandManager {
    override fun handleRegisterEdge(owner: Command, before: Command, after: Command) {
        val before = if (owner == before) this else before
        val after = if (owner == after) this else after
        manager.handleRegisterEdge(owner, before, after)
    }

    override fun handleDeregisterEdge(owner: Command, before: Command, after: Command) {
        val before = if (owner == before) this else before
        val after = if (owner == after) this else after
        manager.handleDeregisterEdge(owner, before, after)
    }

    override fun handleDeregisterAll(src: Command) {
        manager.handleDeregisterAll(src)
    }

    protected fun newCommand(supplier: CommandSupplier)
        = with(CommandContext(manager = this), supplier)

    protected fun close(subcommand: Command) {
        subcommand.cleanup()
        handleDeregisterAll(subcommand)
    }
}