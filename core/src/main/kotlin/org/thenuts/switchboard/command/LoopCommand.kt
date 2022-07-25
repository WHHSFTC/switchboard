package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

// TODO refactor command interface to reuse commands without real time generation
class LoopCommand(val pred: (Frame) -> Boolean, val interrupt: Boolean = false, val commandBuilder: () -> Command) : CommandAbstract(), CommandManager {
    private var cmd: Command? = null
    override var done: Boolean = false

    override fun init() {
        cmd = commandBuilder().also {
            it.setManager(this)
            it.init()
        }
    }

    override fun start(frame: Frame) {
        cmd = commandBuilder().also {
            it.start(frame)
        }
    }

    override fun update(frame: Frame) {
        if ((interrupt || cmd == null) && pred(frame)) {
            cmd?.cleanup()
            cmd = null
            done = true
            return
        }

        val c = cmd ?: commandBuilder().also {
            it.setManager(this)
            it.init()
            it.start(frame)
        }

        c.update(frame)

        if (c.done) {
            c.cleanup()
            cmd = null
        }
    }

    override fun cleanup() {
        cmd?.cleanup()
    }

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