package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class ServoStub(val name: String, val log: Logger): Servo {
    override var position: Double = 0.0
    override fun output(all: Boolean) {
        log.out["[STUB] $name pos"] = position
    }
}