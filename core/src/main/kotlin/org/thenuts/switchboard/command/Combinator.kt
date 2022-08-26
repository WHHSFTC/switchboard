package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

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

    protected fun setup(cmd: Command, frame: Frame) {
        cmd.init(this)
        cmd.start(frame)
    }

    protected fun close(cmd: Command) {
        cmd.cleanup()
        commandManager.handleDeregisterAll(cmd)
    }
}