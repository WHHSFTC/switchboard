package org.thenuts.switchboard.command

import org.thenuts.switchboard.util.Frame

/**
 * Routines that include initialization, loop, and cleanup.
 *
 * The four functions must be called in order:
 * 1. [init] once
 * 2. [start] once
 * 3. [update] zero or more times
 * 4. [cleanup] once
 *
 * Before each call of [update], the command must be cleaned up if [done] is true.
 *
 * None of these functions should contain hardware calls or other long operations (would take less
 * time on the OpMode thread to spawn a new thread for any computation than to compute it directly).
 */
interface Command {
    /**
     * Sets the [manager] and initializes command state.
     * @param manager Handler for ordering and resource requests
     */
    fun init(manager: CommandManager) { }

    /**
     * Called once per command unconditionally.
     * @param frame Current scheduler frame
     */
    fun start(frame: Frame) { }

    /**
     * Called zero or more times, as long as [done] is false.
     * @param frame Current scheduler frame
     */
    fun update(frame: Frame) { }

    /**
     * Cleans up any side effects of the command.
     * Called after [start] or [update] when [done] is true or when interrupted.
     */
    fun cleanup() { }

    /**
     * Whether a command has finished its execution.
     * To be accessed after every call to [start] or [update].
     */
    val done: Boolean

    /**
     * Does nothing and finishes immediately.
     */
    object NOP : Command {
        override val done: Boolean = true
    }

    /**
     * Does nothing and never finishes.
     */
    object IDLE : Command {
        override val done: Boolean = false
    }
}

