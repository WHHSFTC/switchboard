package org.thenuts.switchboard.command

abstract class CommandAbstract : Command {
    private var _postreqs: MutableList<Pair<Command, Int>> = mutableListOf()

    override val postreqs: List<Pair<Command, Int>>
        get() = _postreqs

    private var _prereqs: MutableList<Pair<Command, Int>> = mutableListOf()

    override val prereqs: List<Pair<Command, Int>>
        get() = _prereqs

    protected fun declarePrereq(cmd: Command, priority: Int) {
        _prereqs += cmd to priority
    }

    protected fun declarePostreq(cmd: Command, priority: Int) {
        _postreqs += cmd to priority
    }
}