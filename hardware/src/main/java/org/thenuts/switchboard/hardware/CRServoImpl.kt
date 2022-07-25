package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger
import org.thenuts.switchboard.util.epsilonEquals

class CRServoImpl(val m: CRServo, val name: String, val log: Logger): CRServo {
    init {
        m.power = 0.0
    }

    override var power: Double = 0.0
    override fun output(all: Boolean) {
        power.let {
            if (!(m.power epsilonEquals it)) {
                m.power = it
            }

            log.err["$name power"] = it
        }
    }
}