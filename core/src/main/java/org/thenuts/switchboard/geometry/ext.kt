package org.thenuts.switchboard.geometry

import kotlin.math.*

const val TAU = 2.0 * PI

fun Double.nearest(factor: Double): Double =
    round(this / factor) * factor
fun Double.limit(min: Double, max: Double): Double =
    if (this < min) min else if (this > max) max else this
infix fun Double.limit(range: Double): Double = limit(-range, range)
fun Int.limit(min: Int, max: Int): Int =
    if (this < min) min else if (this > max) max else this

infix fun Double.max(that: Double): Double = kotlin.math.max(this, that)

fun Number.angleWrap(): Double {
    var d = this.toDouble()

    // val zeroToTau = (d % TAU + TAU) % TAU

    while (d > PI) d -= TAU
    while (d < -PI) d += TAU
    return d
}

fun Number.degToRad() =
        Math.toRadians(this.toDouble())

fun Number.radToDeg() =
        Math.toDegrees(this.toDouble())

fun Number.sin() =
        kotlin.math.sin(this.toDouble())

fun Number.cos() =
        kotlin.math.cos(this.toDouble())

val Number.sin get() = sin()
val Number.cos get() = cos()

const val EPSILON = 1e-6

fun Double.epsilonEquals(other: Double, threshold: Double) = abs(this - other) < threshold
infix fun Double.epsilonEquals(other: Double) = this.epsilonEquals(other, EPSILON)
fun Double.epsilonSign() = if (this.epsilonEquals(0.0)) 0.0 else this.sign
fun Double.epsilonReduce(threshold: Double = EPSILON) = if (this.epsilonEquals(0.0, threshold)) 0.0 else this