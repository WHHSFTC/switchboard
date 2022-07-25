package org.thenuts.switchboard.units

class Time private constructor(val nanoseconds: Long) {
    val milliseconds: Double get() = nanoseconds / NANO_IN_MILLI
    val seconds: Double get() = nanoseconds / NANO_IN_SECOND

    operator fun times(that: Number): Time = Time(this.nanoseconds * that.toLong())
    operator fun div(that: Number): Time = Time(this.nanoseconds / that.toLong())
    operator fun plus(that: Time): Time = Time(this.nanoseconds + that.nanoseconds)
    operator fun minus(that: Time): Time = Time(this.nanoseconds - that.nanoseconds)
    operator fun compareTo(that: Time): Int = this.nanoseconds.compareTo(that.nanoseconds)

    infix fun shl(that: Int): Time = Time(this.nanoseconds shl that)
    infix fun shr(that: Int): Time = Time(this.nanoseconds shr that)

    override fun toString(): String = "$seconds s"

    companion object {
        const val NANO_IN_MILLI = 1_000_000.0
        const val MILLI_IN_SECOND = 1_000.0
        const val NANO_IN_SECOND = NANO_IN_MILLI * MILLI_IN_SECOND

        val zero = Time(0)
        fun now() = Time(System.nanoTime())
        fun nano(ns: Long) = Time(ns)
        fun milli(ms: Double) = Time((ms * NANO_IN_MILLI).toLong())
        fun seconds(s: Double) = Time((s * NANO_IN_SECOND).toLong())

        fun milli(ms: Number) = Time.milli(ms.toDouble())
        fun seconds(s: Number) = Time.seconds(s.toDouble())
    }
}

fun Long.ns() = Time.nano(this)
fun Number.ms() = Time.milli(this.toDouble())
fun Number.s() = Time.seconds(this.toDouble())
