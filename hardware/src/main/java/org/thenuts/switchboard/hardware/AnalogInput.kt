package org.thenuts.switchboard.hardware

interface AnalogInput: org.thenuts.switchboard.hardware.HardwareInput {
    val voltage: Double
    val maxVoltage: Double
}