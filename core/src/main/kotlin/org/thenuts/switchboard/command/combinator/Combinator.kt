package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.CommandAbstract
import org.thenuts.switchboard.util.Frame

@Suppress("NAME_SHADOWING")
abstract class Combinator : CommandAbstract() {
//    override fun handleRegisterEdge(owner: Command, before: Command, after: Command) {
//        val before = if (owner == before) this else before
//        val after = if (owner == after) this else after
////        commandManager.handleRegisterEdge(owner, before, after)
//    }
//
//    override fun handleDeregisterEdge(owner: Command, before: Command, after: Command) {
//        val before = if (owner == before) this else before
//        val after = if (owner == after) this else after
////        commandManager.handleDeregisterEdge(owner, before, after)
//    }
//
//    override fun handleDeregisterAll(src: Command) {
////        commandManager.handleDeregisterAll(src)
//    }

    protected fun setup(cmd: Command, frame: Frame) {
        cmd.start(frame)
    }

    protected fun close(cmd: Command) {
        cmd.cleanup()
//        commandManager.handleDeregisterAll(cmd)
    }
}