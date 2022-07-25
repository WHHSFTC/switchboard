package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.units.Time

class AllScheduler(val list: List<HardwareScheduler>): HardwareScheduler {
    override fun getWorstMean(): Time =
        list.fold(Time.zero) { acc, sched -> acc + sched.getWorstMean() }

    override fun output(all: Boolean) {
        list.forEach { it.output() }
    }
}