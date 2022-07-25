package org.thenuts.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.thenuts.switchboard.core.Logger
import org.thenuts.switchboard.util.EPSILON
import org.thenuts.switchboard.util.epsilonEquals
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

class EncoderImpl(val m: DcMotorEx, val name: String, val log: Logger): Encoder {
    var lastTime = Duration.ZERO
    var lastPosition = 0

    var rawVelocity: Double = 0.0

    var rawPosition: Int = 0
        private set
    var offset: Int = 0
        private set

    override var position: Int
        get() = rawPosition + offset
        set(value) {
            offset = value - rawPosition
        }

    override var velocity: Double = 0.0
        private set

    override fun input() {
        rawPosition = m.currentPosition
        rawVelocity = m.velocity

        val t = Duration.sinceJvmTime()
        val step = (t - lastTime).inSeconds

        var v = rawVelocity

        if (lastTime != Duration.ZERO && !(step epsilonEquals 0.0)) {
            val derivative = (rawPosition - lastPosition)/step
            while (derivative - v > (1 shl 16) / 2.0)
                v += 1 shl 16

            while (derivative - v < (1 shl 16) / -2.0)
                v -= 1 shl 16
        }


        velocity = v

        lastPosition = rawPosition
        lastTime = t

        log.err["$name pos"] = rawPosition
        log.err["$name velocity"] = velocity
    }

    override fun stopAndReset() {
        val mode = m.mode
        m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        m.mode = mode
        offset = 0
    }
}