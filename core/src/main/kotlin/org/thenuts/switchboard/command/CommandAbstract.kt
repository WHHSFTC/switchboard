package org.thenuts.switchboard.command

abstract class CommandAbstract : Command {
    protected lateinit var commandManager: CommandManager

    override fun init(manager: CommandManager) {
        commandManager = manager
    }

    fun registerPrerequisite(prereq: Command) {
        commandManager.handleRegisterEdge(this, prereq, this)
    }

    fun registerPostrequisite(postreq: Command) {
        commandManager.handleRegisterEdge(this, this, postreq)
    }
    
    fun deregisterPrerequisite(prereq: Command) {
        commandManager.handleDeregisterEdge(this, prereq, this)
    }

    fun deregisterPostrequisite(postreq: Command) {
        commandManager.handleDeregisterEdge(this, this, postreq)
    }

    fun deregisterAll() {
        commandManager.handleDeregisterAll(this)
    }
}