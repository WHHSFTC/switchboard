package org.thenuts.switchboard.command

import org.thenuts.switchboard.core.Frame

class ConcurrentCommand(val list: List<Command>, val awaitAll: Boolean = true) : CommandAbstract {
    override fun load() {
        list.forEach { it.load() }
    }

    override fun update(frame: Frame) {
        val busy = list.filter { !it.done }

        if (busy.isEmpty() || (!awaitAll && busy.size != list.size)) {
            busy.forEach { it.cleanup() }
            finish()
        } else {
            busy.forEach { it.update(frame) }
        }
    }

    override fun cleanup() {
        val busy = list.filter { !it.done }

        if (busy.isNotEmpty())
            busy.forEach { it.cleanup() }
    }
}