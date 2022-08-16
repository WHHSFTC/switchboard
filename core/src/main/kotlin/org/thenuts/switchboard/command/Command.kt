package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

interface Command {
    fun start(frame: Frame) { }
    fun update(frame: Frame) { }
    fun cleanup() { }

    val done: Boolean

    object NOP : Command {
        override val done: Boolean = true
    }

    object STALL : Command {
        override val done: Boolean = false
    }
}

