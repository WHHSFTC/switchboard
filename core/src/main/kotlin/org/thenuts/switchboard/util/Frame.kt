package org.thenuts.switchboard.util

import kotlin.time.Duration

data class Frame(val n: Long, val runtime: Duration, val step: Duration, val basis: Duration = Duration.ZERO) {
    companion object {
        fun from(basis: Duration, last: Frame)
            = Duration.sinceJvmTime(basis).let {
                Frame(
                        last.n + 1,
                        it,
                        it - last.runtime,
                        basis,
                )
            }
        fun from(last: Frame) = from(last.basis, last)
    }

    val clock = runtime - basis
}

fun Duration.Companion.sinceJvmTime(duration: Duration = Duration.ZERO) = System.nanoTime().nanoseconds - duration
