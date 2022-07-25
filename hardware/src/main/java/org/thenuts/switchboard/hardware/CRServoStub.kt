package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class CRServoStub(val name: String, val log: Logger): CRServo {
    override var power: Double = 0.0

    override fun output(all: Boolean) {
        log.out["[STUB] $name power"] = power
    }
}