package org.thenuts.switchboard.core

import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

data class Frame(val n: Long, val runtime: Duration, val step: Duration) {
    companion object {
        fun from(basis: Duration, last: Frame)
            = Duration.sinceJvmTime(basis).let {
                Frame(
                        last.n + 1,
                        it,
                        it - last.runtime
                )
            }
    }
}