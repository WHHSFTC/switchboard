package org.thenuts.switchboard.scheduler

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun all(vararg elements: HardwareCallable) = AllScheduler(elements.asList())
fun rot(duration: Duration = 3.milliseconds, vararg elements: HardwareCallable) = RotatingScheduler(duration, elements.asList())
fun bucket(duration: Duration = 3.milliseconds, vararg elements: List<HardwareCallable>) = BucketScheduler(duration, elements.asList())
