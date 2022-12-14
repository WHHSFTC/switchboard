package org.thenuts.switchboard.core


import org.firstinspires.ftc.robotcore.external.Telemetry
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

class Logger(
        val telemetry: Telemetry,
) {
    init {
        telemetry.setDisplayFormat(Telemetry.DisplayFormat.MONOSPACE)
    }

    // map of state for telemetry to be updated each cycle
    val out = LogStream("out")
    val err = LogStream("err")

    val receivers: MutableSet<LogReceiver> = mutableSetOf(LogReceiver.TelemetryReciever(telemetry))

    fun addReceiver(receiver: LogReceiver) {
        receivers += receiver
    }

    // list of one-off messages to display
    val messages: MutableList<Pair<String, Duration>> = mutableListOf()
    fun addMessage(text: String, duration: Duration) {
        messages += text to (Duration.sinceJvmTime() + duration)
    }

    fun clear() {
        messages.clear()
        out.clear()
        err.clear()
    }

    fun update() {
        val now = Duration.sinceJvmTime()
        messages.removeIf { (_, end) -> end < now }

//        val t = if (DEBUG) multipleTelemetry else telemetry

        receivers.forEach {
            it.print(out)
            if (DEBUG)
                it.print(err)
        }

        telemetry.addLine("messages")
        telemetry.addLine("---")
        messages.forEach { telemetry.addLine(it.first) }

        telemetry.update()
    }

    inner class LogStream(
            val name: String,
            val mutableMap: MutableMap<String, Any?> = mutableMapOf()
    ): MutableMap<String, Any?> by mutableMap {
        val suppliers: MutableMap<String, () -> Any?> = mutableMapOf()
    }

    interface LogReceiver {
        fun print(l: LogStream)

        open class TelemetryReciever(val t: Telemetry) : LogReceiver {
            override fun print(l: LogStream) {
                t.addLine(l.name)
                t.addLine("---")
                l.mutableMap.forEach { (k, v) -> t.addData(k, v) }
                l.suppliers.forEach { (k, v) -> t.addData(k, v()) }
            }
        }
    }

    companion object {
        @JvmField var DEBUG = false
        @JvmField var INTERVAL = 250
    }
}