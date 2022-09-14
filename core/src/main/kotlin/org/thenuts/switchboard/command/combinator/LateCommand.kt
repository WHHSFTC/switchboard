package org.thenuts.switchboard.command.combinator

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame

/**
 * Uses [supplier] to generate a new Command on start, then executes it.
 */
class LateCommand(val supplier: () -> Command) : Command {
    var cmd: Command? = null
    override var done: Boolean = false
        private set

    override fun start(frame: Frame) {
        done = false
        cmd = supplier()

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
