package org.thenuts.switchboard.scheduler

import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

fun <T> Array<T>.surelyList() = if (isNotEmpty()) asList() else emptyList()
fun all(vararg elements: HardwareScheduler) = AllScheduler(elements.surelyList())
fun rot(duration: Duration = 3.milliseconds, vararg elements: HardwareScheduler) = RotatingScheduler(duration, elements.surelyList())
fun bucket(duration: Duration = 3.milliseconds, vararg elements: List<HardwareScheduler>) = BucketScheduler(duration, elements.surelyList())
