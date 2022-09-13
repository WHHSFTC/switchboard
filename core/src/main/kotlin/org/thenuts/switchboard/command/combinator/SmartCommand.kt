package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

class SmartContext

class SmartCombinator(val supplier: SmartContext.() -> Command) : Command {
    var cmd: Command? = null
    override var done: Boolean = false
        private set

    override fun start(frame: Frame) {
        done = false
        val ctx = SmartContext()
        cmd = with(ctx) { supplier() }

        cmd!!.start(frame)
        done = cmd!!.done
    }

    override fun update(frame: Frame) {
        cmd!!.update(frame)
        done = cmd!!.done
    }

    override fun cleanup() {
        cmd!!.cleanup()
        cmd = null
    }
}
