package org.thenuts.switchboard.command

interface CommandManager {
//    fun offerResource(provider: Command, key: Any, value: Any, mutable: Boolean, internal: Boolean)
//
//    fun requestResource(user: Command, key: Any, mutable: Boolean, internal: Boolean): Any?

    fun handleRegisterPrerequisite(src: Command, prereq: Command)
    fun handleRegisterPostrequisite(src: Command, postreq: Command)

    fun handleDeregisterPrerequisite(src: Command, prereq: Command)
    fun handleDeregisterPostrequisite(src: Command, postreq: Command)

//    fun handleFinish(src: Command)
}