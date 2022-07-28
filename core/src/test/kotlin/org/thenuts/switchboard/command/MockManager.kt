package org.thenuts.switchboard.command

class MockManager : CommandManager {
    override fun handleDeregisterAll(src: Command) { }

    override fun handleDeregisterEdge(owner: Command, before: Command, after: Command) { }

    override fun handleRegisterEdge(owner: Command, before: Command, after: Command) { }
}