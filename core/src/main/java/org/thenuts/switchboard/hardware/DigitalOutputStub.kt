package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class DigitalOutputStub(val name: String, val log: Logger): DigitalOutput {
    override var high: Boolean = false
    override fun output(all: Boolean) {
        log.out["[STUB] $name high"] = high
    }
}