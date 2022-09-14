package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command

abstract class Combinator : Command {
    override var prereqs: List<Pair<Command, Int>> = listOf()
    override var postreqs: List<Pair<Command, Int>> = listOf()

    var subCommands: List<Command> = listOf()
        set(value) {
            prereqs = value.flatMap { it.prereqs }.filter { (cmd, _) -> cmd !in value }
            postreqs = value.flatMap { it.postreqs }.filter { (cmd, _) -> cmd !in value }

            field = value
        }
}