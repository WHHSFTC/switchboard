package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

interface Command {
    fun init() { }
    fun start(frame: Frame) { }
    fun update(frame: Frame) { }
    fun cleanup() { }

    fun setManager(manager: CommandManager) { }
    val done: Boolean

    object nop : Command {
        override val done: Boolean = true
    }

    object stall : Command {
        override val done: Boolean = false
    }
}

