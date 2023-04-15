package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

class EDFScheduler(
    val duration: Duration,
    callables: Map<HardwareCallable, Duration>
) : HardwareCallable {
    private val tasks: Map<HardwareCallable, Task> = callables.mapValues { (_, v) -> Task(v) }
    private val holds: MutableMap<HardwareCallable, Duration> = mutableMapOf()

    @FunctionalInterface
    interface TaskCallback {
        fun completed(currentTime: Duration, deadline: Duration)
    }

    data class Task(
        val defaultPeriod: Duration,
        var deadline: Duration = Duration.ZERO,
        val callbacks: MutableList<TaskCallback> = mutableListOf()
    )

    override fun output(all: Boolean) {
        if (all) {
            tasks.toList().forEach { (c, _) -> c.output(all = true) }
            return
        }

        val start = Duration.sinceJvmTime()
        val end = start + duration
        val queue = tasks.toList().sortedBy { it.second.deadline }.toMutableList()

        while (Duration.sinceJvmTime() < end && queue.isNotEmpty()) {
            queue[0].first.output()
            queue[0].second.deadline = Duration.sinceJvmTime() + getPeriod(queue[0].second)
            queue.removeAt(0)
        }
    }

    fun getPeriod(task: Task): Duration {
        return Duration.ZERO
    }

    override fun getWorstMean(): Duration {
        return duration
    }

    fun scheduleAbsolute(callable: HardwareCallable, deadline: Duration) {
        val current = (tasks[callable] ?: return).deadline
        if (deadline < current)
            tasks[callable]!!.deadline = deadline
    }

    fun scheduleRelative(callable: HardwareCallable, deadline: Duration) {
        scheduleAbsolute(callable, deadline + Duration.sinceJvmTime())
    }
}