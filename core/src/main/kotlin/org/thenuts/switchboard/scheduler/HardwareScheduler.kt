package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.units.Time

interface HardwareScheduler {
    fun output(all: Boolean = false)

    fun getWorstMean(): Time = Time.milli(3)

    object idle : HardwareScheduler {
        override fun getWorstMean(): Time = Time.zero
        override fun output(all: Boolean) { }
    }
}
