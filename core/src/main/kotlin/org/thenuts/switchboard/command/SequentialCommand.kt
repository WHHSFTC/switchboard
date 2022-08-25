package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

class SequentialCommand(private val sequence: Sequence<Command>, val initAll: Boolean) : Combinator() {
    private lateinit var cmd: Command
    private lateinit var iter: Iterator<Command>

    override var done: Boolean = false

    override fun init(ctx: CommandContext) {
        super.init(ctx)

        val cmds = sequence.map {
            it.init(subCtx)
            it
        }

        iter = if (initAll) {
            // collecting sequence into list forces all commands to be inited on SequentialCommand init
            // requires that sequence terminates
            cmds.toList().iterator()
        } else {
            // lazily initializes commands as iter.next() is called, usually ideal behavior
            cmds.iterator()
        }
    }

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