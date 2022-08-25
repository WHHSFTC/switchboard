package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.thenuts.switchboard.util.Frame
import org.thenuts.switchboard.util.sinceJvmTime
import kotlin.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SequentialCommandTest {
    @Test
    fun interruptTest() {
        val manager = MockManager()
        val linear = manager.newCommand {
            val iter = listOf<CommandSupplier>({MockCommand(4)}, {MockCommand(4)}, {MockCommand(4)}).iterator()
            SequentialCommand(Sequence { iter }, true)
        } as SequentialCommand

        var frame = Frame(0, Duration.sinceJvmTime(), Duration.ZERO)
        linear.start(frame)

        repeat(6) {
            if (!linear.done) {
                frame = Frame.from(Duration.ZERO, frame)
                linear.update(frame)
            }
        }

        linear.cleanup()

//        assertArrayEquals(linear.usedCommands.map { true }.toTypedArray(), linear.usedCommands.map { (it as MockCommand).state.isSafe }.toTypedArray())
    }
}