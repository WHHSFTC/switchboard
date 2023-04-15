package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.command.Command
import org.thenuts.switchboard.util.Frame
import kotlin.time.Duration

class HardwareCommand(val callable: HardwareCallable, val edf: EDFScheduler, val deadlineFromStart: Duration) : Command {
    override var done: Boolean = false

    override fun start(frame: Frame) {
        edf.scheduleRelative(callable, deadlineFromStart)
    }

    override fun update(frame: Frame) {

    }
}