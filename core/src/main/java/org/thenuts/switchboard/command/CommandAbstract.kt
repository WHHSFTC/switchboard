package org.thenuts.switchboard.command

abstract class CommandAbstract : Command {
    private lateinit var manager: CommandManager
    override fun setManager(manager: CommandManager) {
        this.manager = manager
    }

    fun registerPrequisite(prereq: Command) {
        this.manager.handleRegisterPrerequisite(this, prereq)
    }

    fun registerPostrequisite(postreq: Command) {
        this.manager.handleRegisterPostrequisite(this, postreq)
    }
    
    fun deregisterPrequisite(prereq: Command) {
        this.manager.handleDeregisterPrerequisite(this, prereq)
    }

    fun deregisterPostrequisite(postreq: Command) {
        this.manager.handleDeregisterPostrequisite(this, postreq)
    }

//    fun finish() {
//        this.manager.handleFinish(this)
//    }
}