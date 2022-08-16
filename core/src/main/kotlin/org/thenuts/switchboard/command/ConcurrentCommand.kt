package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

context(CommandContext)
class ConcurrentCommand(val list: List<CommandSupplier>, val awaitAll: Boolean = true) : Combinator() {
    val cmds = list.map { newCommand(it) }.toMutableList()

    override var done: Boolean = false

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