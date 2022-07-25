package org.thenuts.switchboard.hardware

import com.qualcomm.robotcore.hardware.DcMotor
import com.qualcomm.robotcore.hardware.DcMotorEx
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit
import org.thenuts.switchboard.geometry.epsilonEquals
import org.thenuts.switchboard.core.Logger

class MotorImpl(val m: DcMotorEx, val name: String, val log: Logger): Motor {
    //var conf: Power = Power(0.0)
    //open class Power(val pow: Double, val zpb: DcMotorEx.ZeroPowerBehavior = DcMotorEx.ZeroPowerBehavior.BRAKE) {
    //object BRAKE : Power(0.0, DcMotorEx.ZeroPowerBehavior.BRAKE)
    //object FLOAT : Power(0.0, DcMotorEx.ZeroPowerBehavior.FLOAT)
    //}
    init {
        m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
        m.power = 0.0
        m.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.FLOAT
        m.zeroPowerBehavior = DcMotor.ZeroPowerBehavior.BRAKE
        m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        m.mode = DcMotor.RunMode.RUN_WITHOUT_ENCODER
    }

    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE
    override var mode: Motor.RunMode = Motor.RunMode.Direct
    var n = 0
        private set
    override fun output(all: Boolean) {
        power.let {
            if (!(m.power epsilonEquals it)) {
                m.power = it
                n++
                log.err["$name count"] = n
            }

            log.out["$name power"] = it
        }

        zpb.let {
            if (m.zeroPowerBehavior != it.mirror)
                m.zeroPowerBehavior = it.mirror
            log.err["$name zpb"] = it
        }

        mode.let {
            if (m.mode != it.mirror) {
                m.mode = it.mirror
                when (it) {
                    is Motor.RunMode.PositionPIDF -> {
                        m.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, it.vpid)
                        m.setPositionPIDFCoefficients(it.pos_p)
                    }

                    is Motor.RunMode.VelocityPIDF -> {
                        m.setPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER, it.vpid)
                    }
                }
            }
            log.err["$name mode"] = it
        }
    }

    override fun reset() {
        m.mode = DcMotor.RunMode.STOP_AND_RESET_ENCODER
        m.mode = mode.mirror
    }

    override val current: Double get() = m.getCurrent(CurrentUnit.AMPS)
}