package org.thenuts.switchboard.scheduler

import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

interface HardwareScheduler {
    fun output(all: Boolean = false)

    fun getWorstMean(): Duration = 3.milliseconds

    object IDLE : HardwareScheduler {
        override fun getWorstMean(): Duration = ZERO
        override fun output(all: Boolean) { }
    }
}
