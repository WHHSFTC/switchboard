package org.thenuts.switchboard.command

data class CommandContext(
    val manager: CommandManager
)

typealias CommandSupplier = CommandContext.() -> Command