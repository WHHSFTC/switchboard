package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class ConcurrentCommand(val list: List<CommandGenerator>, val awaitAll: Boolean = true) : Combinator() {
    lateinit var cmds: MutableList<Command>

    override var done: Boolean = false

    override fun init() {
        super.init()

        cmds = list.map {
            with(subCtx) { it.init() }
        }.toMutableList()
    }

    override fun start(frame: Frame) {
        cmds.forEach { it.start(frame) }
    }

    override fun update(frame: Frame) {
        cmds.removeAll {
            if (it.done) {
                close(it)
                true
            } else false
        }

        if (cmds.isEmpty() || (!awaitAll && cmds.size != list.size)) {
            cmds.forEach { close(it) }
            cmds.clear()
            done = true
        } else {
            cmds.forEach { it.update(frame) }
        }
    }

    override fun cleanup() {
        cmds.forEach {
            close(it)
        }
        cmds.clear()
    }
}