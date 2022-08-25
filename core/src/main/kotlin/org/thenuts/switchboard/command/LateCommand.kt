package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class LateCommand(val supplier: CommandSupplier) : Combinator() {
    override var done: Boolean = false
        private set

    var cmd: Command? = null
        private set

    override fun start(frame: Frame) {
        cmd = subCtx.supplier()
        cmd!!.start(frame)

        done = cmd!!.done
    }

    override fun update(frame: Frame) {
        if (done) return

        cmd!!.update(frame)

        if (cmd!!.done) {
            close(cmd!!)
            done = true
        }
    }

    override fun cleanup() {
        if (!done) {
            close(cmd!!)
            done = true
        }
    }
}