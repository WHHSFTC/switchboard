package org.thenuts.switchboard.hardware

import com.qualcomm.robotcore.hardware.ServoImplEx
import org.thenuts.switchboard.core.Logger
import org.thenuts.switchboard.util.epsilonEquals

class ServoImpl(val s: com.qualcomm.robotcore.hardware.Servo, val name: String, val log: Logger): Servo {
    //override var position: Double = 0.0
    override var position: Double = s.position
    var touched = false
        private set
    var n = 0
        private set
    var justEnabled = false
        private set
    var justDisabled = false
        private set
    override fun output(all: Boolean) {
        if (justDisabled) {
            (s as? ServoImplEx)?.let {
                if (it.isPwmEnabled) {
                    it.setPwmDisable()
                }
            }
            justDisabled = false
            return
        } else if (justEnabled) {
            (s as? ServoImplEx)?.let {
                if (!it.isPwmEnabled) {
                    it.setPwmEnable()
                }
            }
            justEnabled = false
        }
        val g = s.position
        if (all || !touched || !(g epsilonEquals position)) {
            if (!touched) {
                s.position = position + if (position < .5) +0.05 else -0.05 // i guess this helps wake up the servo
//                s.position = position.limit(0.05, 0.95) // savox ???
            }

            s.position = position

            log.err["$name touched"] = touched
            n++
            touched = true

            log.err["$name pos"] = position
            log.err["$name get"] = g
            log.err["$name count"] = n
        }
    }

    fun disable() {
        justEnabled = false
        justDisabled = true
    }

    fun enable() {
        justDisabled = false
        justEnabled = true
    }
}