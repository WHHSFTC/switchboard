package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.units.Time
import java.util.*

class RotatingScheduler(val duration: Time, val list: List<HardwareScheduler>): HardwareScheduler {
    val queue: Queue<HardwareScheduler> = LinkedList(list)
    override fun getWorstMean(): Time = duration
    override fun output(all: Boolean) {
        if (all) return queue.forEach { it.output(all = true) }
        val start = Time.now()
        val end = start + duration
        val tmp = LinkedList<HardwareScheduler>()
        while (Time.now() < end && queue.isNotEmpty())
            queue.poll()?.also { it.output(); tmp.add(it) }
        queue.addAll(tmp)
    }
}