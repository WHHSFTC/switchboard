package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class AnalogInputImpl(val ai: com.qualcomm.robotcore.hardware.AnalogInput, val name: String, val log: Logger):
    org.thenuts.switchboard.hardware.AnalogInput {
    override var voltage: Double = 0.0
        private set
    override val maxVoltage: Double get() = ai.maxVoltage
    override fun input() {
        voltage = ai.voltage
        log.err["$name voltage"] = voltage
    }
}