package org.thenuts.switchboard.scheduler

import kotlin.time.Duration


class AllScheduler(val list: List<HardwareScheduler>): HardwareScheduler {
    override fun getWorstMean(): Duration =
        list.fold(Duration.ZERO) { acc, sched -> acc + sched.getWorstMean() }

    override fun output(all: Boolean) {
        list.forEach { it.output() }
    }
}