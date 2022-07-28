package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame
import kotlin.time.Duration

class DelayCommand(val duration: Duration) : Command {
    override var done: Boolean = false

    var startTime = Duration.ZERO

    override fun start(frame: Frame) {
        startTime = frame.runtime
    }

    override fun update(frame: Frame) {
        if (frame.runtime - startTime > duration)
            done = true
    }
}