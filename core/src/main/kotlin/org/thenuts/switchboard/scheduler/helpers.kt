package org.thenuts.switchboard.scheduler

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun all(vararg elements: HardwareScheduler) = AllScheduler(elements.asList())
fun rot(duration: Duration = 3.milliseconds, vararg elements: HardwareScheduler) = RotatingScheduler(duration, elements.asList())
fun bucket(duration: Duration = 3.milliseconds, vararg elements: List<HardwareScheduler>) = BucketScheduler(duration, elements.asList())
