package org.thenuts.switchboard.command

import org.thenuts.switchboard.command.store.ResourceHandler

abstract class CommandAbstract : Command {
    private var _postreqs: MutableList<Pair<Command, Int>> = mutableListOf()

    override val postreqs: List<Pair<Command, Int>>
        get() = _postreqs

    private var _prereqs: MutableList<Pair<Command, Int>> = mutableListOf()

    override val prereqs: List<Pair<Command, Int>>
        get() = _prereqs

    private var _dependencies: MutableMap<Any, ResourceHandler<*>> = mutableMapOf()

    override val dependencies: Map<Any, ResourceHandler<*>>
        get() = _dependencies

    protected fun declarePrereq(cmd: Command, priority: Int) {
        _prereqs += cmd to priority
    }

    protected fun declarePostreq(cmd: Command, priority: Int) {
        _postreqs += cmd to priority
    }

    protected fun declareDependency(key: Any, resourceHandler: ResourceHandler<*>) {
        _dependencies[key] = resourceHandler
    }
}