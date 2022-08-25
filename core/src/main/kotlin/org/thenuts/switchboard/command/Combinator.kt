package org.thenuts.switchboard.command

@Suppress("NAME_SHADOWING")
abstract class Combinator : CommandAbstract(), CommandManager {
    protected lateinit var subCtx: CommandContext
        private set

    override fun init() {
        subCtx = ctx.copy(manager = this)
    }

    override fun handleRegisterEdge(owner: Command, before: Command, after: Command) {
        val before = if (owner == before) this else before
        val after = if (owner == after) this else after
        ctx.manager.handleRegisterEdge(owner, before, after)
    }

    override fun handleDeregisterEdge(owner: Command, before: Command, after: Command) {
        val before = if (owner == before) this else before
        val after = if (owner == after) this else after
        ctx.manager.handleDeregisterEdge(owner, before, after)
    }

    override fun handleDeregisterAll(src: Command) {
        ctx.manager.handleDeregisterAll(src)
    }

    protected fun close(subcommand: Command) {
        subcommand.cleanup()
        handleDeregisterAll(subcommand)
    }
}