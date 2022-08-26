package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ConcurrentCommandTest {
    @Test
    fun interruptTest() {
        val manager = MockManager()
        val concurrent = ConcurrentCommand(listOf(MockCommand(4), MockCommand(4), MockCommand(4)))

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        concurrent.start(frame)

        repeat(6) {
            if (!concurrent.done) {
                frame = Frame.from(Duration.ZERO, frame)
                concurrent.update(frame)
            }
        }

        concurrent.cleanup()

        assertArrayEquals(concurrent.list.map { true }.toTypedArray(), concurrent.list.map { (it as MockCommand).state.isSafe }.toTypedArray())
    }

    @Test
    fun awaitAllTest() {
        val manager = MockManager()
        val concurrent = ConcurrentCommand(listOf(MockCommand(4), MockCommand(4), MockCommand(4)), awaitAll = true)

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        concurrent.start(frame)

        while (!concurrent.done) {
            frame = Frame.from(Duration.ZERO, frame)
            concurrent.update(frame)
        }

        concurrent.cleanup()

        assertArrayEquals(concurrent.list.map { true }.toTypedArray(), concurrent.list.map { it.done }.toTypedArray())
        assertArrayEquals(concurrent.list.map { true }.toTypedArray(), concurrent.list.map { (it as MockCommand).state.isSafe }.toTypedArray())
    }

    @Test
    fun raceTest() {
        val manager = MockManager()
        val concurrent = ConcurrentCommand(listOf(MockCommand(4), MockCommand(3), MockCommand(4)), awaitAll = false)

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        concurrent.start(frame)

        while (!concurrent.done) {
            frame = Frame.from(Duration.ZERO, frame)
            concurrent.update(frame)
        }

        concurrent.cleanup()

        assertArrayEquals(listOf(false, true, false).toTypedArray(), concurrent.list.map { it.done }.toTypedArray())
        assertArrayEquals(concurrent.list.map { true }.toTypedArray(), concurrent.list.map { (it as MockCommand).state.isSafe }.toTypedArray())
    }
}