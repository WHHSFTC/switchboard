package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame
import org.thenuts.switchboard.units.Time

class DelayCommand(val duration: Time) : Command {
    var start = Time.zero
    override var done: Boolean = false
    
    override fun load(frame: Frame) {
        start = frame.runtime
    }

    override fun update(frame: Frame) {
        if (!done && frame.runtime - start > duration)
            done = true
    }
}