package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

context(CommandContext)
class SequentialCommand(sequence: Sequence<CommandSupplier>, pregen: Boolean) : Combinator() {
    private lateinit var cmd: Command
    val iter: Iterator<Command>

    init {
        iter = if (pregen) {
            sequence.toList().map { newCommand(it) }.iterator()
        } else {
            sequence.map { newCommand(it) }.iterator()
        }
    }

    override var done: Boolean = false

    override fun start(frame: Frame) {
        while (true) {
            if (!iter.hasNext()) {
                done = true
                return
            }

            cmd = iter.next()
            cmd.start(frame)

            if (!cmd.done)
                return

            close(cmd)
        }
    }

    override fun update(frame: Frame) {
        while (true) {
            while (cmd.done) {
                close(cmd)

                if (!iter.hasNext()) {
                    done = true
                    return
                }

                cmd = iter.next()
                cmd.start(frame)
            }

            cmd.update(frame)

            if (!cmd.done)
                return
        }
    }

    override fun cleanup() {
        if (!done) {
            close(cmd)
            done = true
        }
    }
}