package org.thenuts.switchboard.command

/**
 * Interface to request edges between Commands and Resources from a Command
 */
interface CommandManager {
    fun handleRegisterEdge(owner: Command, before: Command, after: Command)

    fun handleDeregisterEdge(owner: Command, before: Command, after: Command)

    fun handleDeregisterAll(src: Command)

//    fun offerResource(scope: Command, key: Any, value: Any, mutable: Boolean, internal: Boolean)
//    fun requestResource(user: Command, key: Any, mutable: Boolean, internal: Boolean): Any?
}