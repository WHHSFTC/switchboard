package org.thenuts.switchboard.command

abstract class Combinator : CommandAbstract(), CommandManager {
    override fun handleRegisterPrerequisite(src: Command, prereq: Command) {
        registerPrequisite(prereq)
    }

    override fun handleRegisterPostrequisite(src: Command, postreq: Command) {
        registerPostrequisite(postreq)
    }

    override fun handleDeregisterPrerequisite(src: Command, prereq: Command) {
        deregisterPrequisite(prereq)
    }

    override fun handleDeregisterPostrequisite(src: Command, postreq: Command) {
        deregisterPostrequisite(postreq)
    }

    override fun handleDeregisterAll(src: Command) {
        deregisterAll()
    }
}