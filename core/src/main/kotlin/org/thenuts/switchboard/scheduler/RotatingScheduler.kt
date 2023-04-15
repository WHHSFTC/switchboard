package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.util.sinceJvmTime
import java.util.*
import kotlin.time.Duration

class RotatingScheduler(val duration: Duration, val list: List<HardwareCallable>): HardwareCallable {
    val queue: Queue<HardwareCallable> = LinkedList(list)
    override fun getWorstMean(): Duration = duration
    override fun output(all: Boolean) {
        if (all) return queue.forEach { it.output(all = true) }
        val start = Duration.sinceJvmTime()
        val end = start + duration
        val tmp = LinkedList<HardwareCallable>()
        while (Duration.sinceJvmTime() < end && queue.isNotEmpty())
            queue.poll()?.also { it.output(); tmp.add(it) }
        queue.addAll(tmp)
    }
}