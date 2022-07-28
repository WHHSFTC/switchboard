package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class SwitchCommand<T>(val supplier: () -> T, val cases: List<Case<T>>) : CommandAbstract(), CommandManager {
    override var done: Boolean = false
    private lateinit var cmd: Command

    override fun start(frame: Frame) {
        val v = supplier()
        for (c in cases) {
            if (c.pred(v)) {
                cmd = c.command
                cmd.setManager(this)
                cmd.init()
                cmd.start(frame)
                return
            }
        }
        cmd = Command.nop
    }

    override fun update(frame: Frame) {
        cmd.update(frame)
        if (cmd.done) {
            done = true
            cmd.cleanup()
        }
    }

    override fun cleanup() {
        if (!done) {
            cmd.cleanup()
        }
    }

    data class Case<T>(val pred: (T) -> Boolean, val command: Command)

    override fun handleRegisterPrerequisite(src: Command, prereq: Command) {
        registerPrequisite(prereq)
    }

    override fun handleRegisterPostrequisite(src: Command, postreq: Command) {
        registerPostrequisite(postreq)
    }

    override fun handleDeregisterPrerequisite(src: Command, prereq: Command) {
        deregisterPrequisite(prereq)
    }

    override fun handleDeregisterPostrequisite(src: Command, postreq: Command) {
        deregisterPostrequisite(postreq)
    }
}