package org.thenuts.switchboard.command

class MockManager : CommandManager {
    override fun handleRegisterPrerequisite(src: Command, prereq: Command) { }
    override fun handleRegisterPostrequisite(src: Command, postreq: Command) { }
    override fun handleDeregisterPrerequisite(src: Command, prereq: Command) { }
    override fun handleDeregisterPostrequisite(src: Command, postreq: Command) { }
}