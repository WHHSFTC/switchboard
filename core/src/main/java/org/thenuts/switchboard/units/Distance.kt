package org.thenuts.switchboard.units

class Distance private constructor(val inch: Double) {
    val ft get() = inch / INCH_IN_FOOT
    val cm get() = inch * CM_IN_INCH
    val mm get() = inch * MM_IN_INCH

    operator fun times(that: Double): Distance = Distance(this.inch * that)
    operator fun div(that: Double): Distance = Distance(this.inch / that)
    operator fun plus(that: Distance): Distance = Distance(this.inch + that.inch)
    operator fun minus(that: Distance): Distance = Distance(this.inch - that.inch)
    operator fun compareTo(that: Distance): Int = this.inch.compareTo(that.inch)

    override fun toString(): String = "$inch in"

    companion object {
        const val INCH_IN_FOOT = 12.0
        const val CM_IN_INCH = 2.54
        const val MM_IN_CM = 10.0
        const val MM_IN_INCH = MM_IN_CM * CM_IN_INCH

        val zero = Distance(0.0)
        fun ft(ft: Double) = Distance(ft * INCH_IN_FOOT)
        fun inch(inches: Double) = Distance(inches)
        fun mm(mm: Double) = Distance(mm / MM_IN_INCH)
        fun cm(cm: Double) = Distance(cm / CM_IN_INCH)
    }
}

fun Number.ft() = Distance.ft(this.toDouble())
fun Number.inch() = Distance.inch(this.toDouble())
fun Number.mm() = Distance.mm(this.toDouble())
fun Number.cm() = Distance.cm(this.toDouble())
