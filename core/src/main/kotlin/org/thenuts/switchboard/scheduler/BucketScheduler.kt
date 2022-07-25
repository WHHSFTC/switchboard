package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.units.Time
import kotlin.math.max

class BucketScheduler(val duration: Time, val table: List<List<HardwareScheduler>>) : HardwareScheduler {
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

    override fun getWorstMean(): Time = Time.nano(max(duration.nanoseconds, table.foldIndexed(Time.zero) { index, acc, list ->
        acc + list.fold(Time.zero) { acc2, sched -> acc2 + (sched.getWorstMean() shr index) }
    }.nanoseconds))

    private var n: Long = 0
    override fun output(all: Boolean) {
        if (all) return table.forEach { it.forEach { it.output(all = true) } }
        val start = Time.now()
        val end = start + duration
        table.forEachIndexed { i, l ->
            val period = 1L shl i
            l.forEachIndexed { j, hw ->
                if (Time.now() > end) {
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