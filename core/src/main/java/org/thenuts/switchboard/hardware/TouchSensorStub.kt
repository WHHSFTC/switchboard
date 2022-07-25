package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class TouchSensorStub(val name: String, val log: Logger): TouchSensor {
    override val pressed: Boolean = false
    override fun input() {
        log.out["[STUB] $name pressed"] = pressed
    }
}