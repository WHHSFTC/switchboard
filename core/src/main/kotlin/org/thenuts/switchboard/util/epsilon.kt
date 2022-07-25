package org.thenuts.switchboard.util

import kotlin.math.abs
import kotlin.math.sign

const val EPSILON = 1e-6

fun Double.epsilonEquals(other: Double, threshold: Double) = abs(this - other) < threshold
infix fun Double.epsilonEquals(other: Double) = this.epsilonEquals(other, EPSILON)
fun Double.epsilonSign() = if (this.epsilonEquals(0.0)) 0.0 else this.sign
fun Double.epsilonReduce(threshold: Double = EPSILON) = if (this.epsilonEquals(0.0, threshold)) 0.0 else this