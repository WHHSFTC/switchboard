package org.thenuts.switchboard.hardware

import com.qualcomm.robotcore.hardware.DigitalChannel
import org.thenuts.switchboard.core.Logger

class DigitalOutputImpl(val dc: DigitalChannel, val name: String, val log: Logger): DigitalOutput {
    override var high: Boolean = false
    override fun output(all: Boolean) {
        if (dc.mode != DigitalChannel.Mode.OUTPUT)
            dc.mode = DigitalChannel.Mode.OUTPUT
        if (dc.state != high)
            dc.state = high

        log.err["$name high"] = high
    }
}