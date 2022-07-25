package org.thenuts.switchboard.hardware

interface TouchSensor: HardwareInput {
    val pressed: Boolean
}