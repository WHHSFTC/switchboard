package org.thenuts.switchboard.hardware

interface AnalogInput: HardwareInput {
    val voltage: Double
    val maxVoltage: Double
}