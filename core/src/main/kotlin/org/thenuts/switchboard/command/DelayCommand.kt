package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame
import kotlin.time.Duration

/**
 * Command that idles until [duration] from when it starts.
 */
class DelayCommand(val duration: Duration) : Command {
    override var done: Boolean = false

    var startTime = Duration.ZERO

    override fun start(frame: Frame) {
        startTime = frame.runtime
        done = false
    }

    override fun update(frame: Frame) {
        if (frame.runtime - startTime > duration)
            done = true
    }
}