package org.thenuts.switchboard.hardware

import org.thenuts.switchboard.core.Logger

class EncoderStub(val name: String, val log: Logger): Encoder {
    override var position: Int = 0
    override val velocity: Double = 0.0

    override fun input() {
        log.out["[STUB] $name pos"] = position
        log.out["[STUB] $name velocity"] = velocity
    }

    override fun stopAndReset() { }
}