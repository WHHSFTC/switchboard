package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class MotorStub(val name: String, val log: Logger): Motor {
    override var power: Double = 0.0
    override var zpb: Motor.ZeroPowerBehavior = Motor.ZeroPowerBehavior.BRAKE
    override var mode: Motor.RunMode = Motor.RunMode.RUN_WITHOUT_ENCODER

    override fun output(all: Boolean) {
        log.out["[STUB] $name power"] = power
        log.out["[STUB] $name zpb"] = zpb
        log.out["[STUB] $name mode"] = mode
    }

    override fun reset() {
        // idk
    }

    override val current: Double
        get() = 0.0
}