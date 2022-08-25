package org.thenuts.switchboard.command

abstract class CommandAbstract : Command, CommandGenerator {
    protected lateinit var ctx: CommandContext
        private set

    abstract fun init()

    context(CommandContext)
    override fun init(): Command {
        this.ctx = this@CommandContext
        init()
        return this
    }

    internal fun registerPrerequisite(prereq: Command) {
        ctx.manager.handleRegisterEdge(this, prereq, this)
    }

    internal fun registerPostrequisite(postreq: Command) {
        ctx.manager.handleRegisterEdge(this, this, postreq)
    }
    
    internal fun deregisterPrerequisite(prereq: Command) {
        ctx.manager.handleDeregisterEdge(this, prereq, this)
    }

    internal fun deregisterPostrequisite(postreq: Command) {
        ctx.manager.handleDeregisterEdge(this, this, postreq)
    }

    internal fun deregisterAll() {
        ctx.manager.handleDeregisterAll(this)
    }
}