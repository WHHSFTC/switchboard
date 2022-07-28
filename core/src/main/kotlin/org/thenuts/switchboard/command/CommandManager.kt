package org.thenuts.switchboard.command

interface CommandManager {
    fun handleRegisterPrerequisite(src: Command, prereq: Command)
    fun handleRegisterPostrequisite(src: Command, postreq: Command)

    fun handleDeregisterPrerequisite(src: Command, prereq: Command)
    fun handleDeregisterPostrequisite(src: Command, postreq: Command)

    fun handleDeregisterAll(src: Command)

//    fun offerResource(scope: Command, key: Any, value: Any, mutable: Boolean, internal: Boolean)
//    fun requestResource(user: Command, key: Any, mutable: Boolean, internal: Boolean): Any?
}