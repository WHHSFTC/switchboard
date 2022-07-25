package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class TouchSensorImpl(val ts: com.qualcomm.robotcore.hardware.TouchSensor, val name: String, val log: Logger): TouchSensor {
    override var pressed: Boolean = false
        private set
    override fun input() {
        pressed = ts.isPressed
        log.out["$name pressed"] = pressed
    }
}