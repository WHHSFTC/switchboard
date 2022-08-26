package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

data class SmartContext(
    val manager: CommandManager
)

class SmartCombinator(val supplier: SmartContext.() -> Command) : Combinator() {
    var cmd: Command? = null
    override var done: Boolean = false
        private set

    override fun init(manager: CommandManager) {
        super.init(manager)
        done = false
        val ctx = SmartContext(manager = this)
        cmd = with(ctx) { supplier() }
    }

    override fun start(frame: Frame) {
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
