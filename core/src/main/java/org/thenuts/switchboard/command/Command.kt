package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

interface Command {
    fun load()
    fun update(frame: Frame)
    fun cleanup()

    fun setManager(manager: CommandManager)
    val done: Boolean
}

