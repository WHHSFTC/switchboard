package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.command.CommandManager
import org.thenuts.switchboard.util.Frame

data class SmartContext(
    val manager: CommandManager
)

class SmartCombinator(val supplier: SmartContext.() -> Command) : Combinator() {
    var cmd: Command? = null
    override var done: Boolean = false
        private set

    override fun start(frame: Frame) {
        done = false
        val ctx = SmartContext(manager = this)
        cmd = with(ctx) { supplier() }

        cmd!!.start(frame)
        done = cmd!!.done
    }

    override fun update(frame: Frame) {
        cmd!!.update(frame)
        done = cmd!!.done
    }

    override fun cleanup() {
        close(cmd!!)
        cmd = null
    }
}
