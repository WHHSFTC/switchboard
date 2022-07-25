package org.thenuts.switchboard.scheduler

import org.thenuts.switchboard.units.Time

fun <T> Array<T>.surelyList() = if (isNotEmpty()) asList() else emptyList()
fun all(vararg elements: HardwareScheduler) = AllScheduler(elements.surelyList())
fun rot(duration: Time = Time.milli(2), vararg elements: HardwareScheduler) = RotatingScheduler(duration, elements.surelyList())
fun bucket(duration: Time = Time.milli(2), vararg elements: List<HardwareScheduler>) = BucketScheduler(duration, elements.surelyList())