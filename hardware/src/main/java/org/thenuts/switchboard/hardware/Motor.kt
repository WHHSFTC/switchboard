package org.thenuts.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.PIDFCoefficients

interface Motor: HardwareOutput {
    enum class ZeroPowerBehavior(val mirror: DcMotor.ZeroPowerBehavior) {
        BRAKE(DcMotor.ZeroPowerBehavior.BRAKE), FLOAT(DcMotor.ZeroPowerBehavior.FLOAT);

        companion object {
            fun mirrorOf(mode: DcMotor.ZeroPowerBehavior)
                = when (mode) {
                    DcMotor.ZeroPowerBehavior.BRAKE -> BRAKE
                    DcMotor.ZeroPowerBehavior.FLOAT -> FLOAT
                    DcMotor.ZeroPowerBehavior.UNKNOWN -> FLOAT
                }
        }
    }
    sealed class RunMode(val mirror: DcMotor.RunMode, val vpid: PIDFCoefficients) {
        class PositionPIDF(val pos_p: Double, vpid: PIDFCoefficients) : RunMode(DcMotor.RunMode.RUN_TO_POSITION, vpid) {
            override fun toString(): String =
                String.format("PositionPIDF(pos_p = %d, p = %d, i = %d, d = %d, f = %d)", pos_p, vpid.p, vpid.i, vpid.d, vpid.f)
        }

        class VelocityPIDF(vpid: PIDFCoefficients) : RunMode(DcMotor.RunMode.RUN_USING_ENCODER, vpid) {
            override fun toString(): String =
                String.format("VelocityPIDF(p = %d, i = %d, d = %d, f = %d)", vpid.p, vpid.i, vpid.d, vpid.f)
        }

        object Direct : RunMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER, PIDFCoefficients()) {
            override fun toString(): String = "Direct Power"
        }

        companion object {
            fun RUN_TO_POSITION(p: Double, vpid: PIDFCoefficients) = PositionPIDF(p, vpid)
            fun RUN_USING_ENCODER(vpid: PIDFCoefficients) = VelocityPIDF(vpid)
            val RUN_WITHOUT_ENCODER = Direct
        }
    }

    fun reset()

    val current: Double

    var power: Double
    var zpb: ZeroPowerBehavior
    var mode: RunMode
}