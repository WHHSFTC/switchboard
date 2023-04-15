package org.thenuts.switchboard.scheduler

import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.Duration.Companion.milliseconds

@FunctionalInterface
interface HardwareCallable {
    fun output(all: Boolean = false)

    fun getWorstMean(): Duration = 3.milliseconds

    object IDLE : HardwareCallable {
        override fun getWorstMean(): Duration = ZERO
        override fun output(all: Boolean) { }
    }
}
