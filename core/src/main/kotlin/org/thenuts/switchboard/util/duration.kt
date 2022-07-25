package org.thenuts.switchboard.util

import kotlin.time.Duration

fun Duration.Companion.sinceJvmTime(duration: Duration = Duration.ZERO) = System.nanoTime().nanoseconds - duration
