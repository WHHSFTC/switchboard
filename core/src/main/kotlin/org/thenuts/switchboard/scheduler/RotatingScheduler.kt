package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.util.sinceJvmTime
import java.util.*
import kotlin.time.Duration

class RotatingScheduler(val duration: Duration, val list: List<HardwareScheduler>): HardwareScheduler {
    val queue: Queue<HardwareScheduler> = LinkedList(list)
    override fun getWorstMean(): Duration = duration
    override fun output(all: Boolean) {
        if (all) return queue.forEach { it.output(all = true) }
        val start = Duration.sinceJvmTime()
        val end = start + duration
        val tmp = LinkedList<HardwareScheduler>()
        while (Duration.sinceJvmTime() < end && queue.isNotEmpty())
            queue.poll()?.also { it.output(); tmp.add(it) }
        queue.addAll(tmp)
    }
}