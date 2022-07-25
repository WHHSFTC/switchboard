package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration
import kotlin.math.max
import kotlin.time.Duration.Companion.nanoseconds

class BucketScheduler(val duration: Duration, val table: List<List<HardwareScheduler>>) : HardwareScheduler {
    /*
    A = 0 1 2 3 4 5 6 7 8 9 a b c d e f

    B = 0   2   4   6   8   a   c   e
    C =   1   3   5   7   9   b   d   f

    D = 0       4       8       c
    E =   1       5       9       d
    F =     2       6       a       e
    G =       3       7       b       f

    table = [
        [A],
        [B, C],
        [D, E, F, G]
    ] -> becomes loop { ABD ACE ABF ACG }
     */

    override fun getWorstMean(): Duration = max(duration.inWholeNanoseconds, table.foldIndexed(Duration.ZERO) { index, acc, list ->
        acc + list.fold(Duration.ZERO) { acc2, sched -> acc2 + (sched.getWorstMean().inWholeNanoseconds shr index).nanoseconds }
    }.inWholeNanoseconds).nanoseconds

    private var n: Long = 0
    override fun output(all: Boolean) {
        if (all) return table.forEach { it.forEach { it.output(all = true) } }
        val start = Duration.sinceJvmTime()
        val end = start + duration
        table.forEachIndexed { i, l ->
            val period = 1L shl i
            l.forEachIndexed { j, hw ->
                if (Duration.sinceJvmTime() > end) {
                    n++
                    return@output
                }

                if ((n - j) and (period - 1L) == 0L) { // n % period == j
                    hw.output()
                }
            }
        }
        n++
    }
}