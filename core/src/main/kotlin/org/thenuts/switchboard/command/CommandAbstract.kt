package org.thenuts.switchboard.command

context(CommandContext)
abstract class CommandAbstract : Command {
    internal fun registerPrerequisite(prereq: Command) {
        manager.handleRegisterEdge(this, prereq, this)
    }

    internal fun registerPostrequisite(postreq: Command) {
        manager.handleRegisterEdge(this, this, postreq)
    }
    
    internal fun deregisterPrerequisite(prereq: Command) {
        manager.handleDeregisterEdge(this, prereq, this)
    }

    internal fun deregisterPostrequisite(postreq: Command) {
        manager.handleDeregisterEdge(this, this, postreq)
    }

    internal fun deregisterAll() {
        manager.handleDeregisterAll(this)
    }
}