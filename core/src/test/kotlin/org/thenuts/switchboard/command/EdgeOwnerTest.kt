package org.thenuts.switchboard.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.thenuts.switchboard.command.CommandScheduler.Edge.Owner.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EdgeOwnerTest {
    @Test
    fun testOwnerAddition() {
        val additions = listOf(
            Triple(DELETE, DELETE, DELETE),
            Triple(DELETE, AFTER, AFTER),
            Triple(DELETE, BEFORE, BEFORE),
            Triple(DELETE, BOTH, BOTH),

            Triple(AFTER, AFTER, AFTER),
            Triple(AFTER, BEFORE, BOTH),
            Triple(AFTER, BOTH, BOTH),

            Triple(BEFORE, BEFORE, BEFORE),
            Triple(BEFORE, BOTH, BOTH),

            Triple(BOTH, BOTH, BOTH)
        )

        additions.forEach { (a, b, c) ->
            assertEquals(c, a + b, String.format("%s + %s != %s", a, b, c))
            assertEquals(c, b + a, String.format("%s + %s != %s", b, a, c))
        }
    }

    @Test
    fun testOwnerSubtraction() {
        val subtractions = listOf(
            Triple(DELETE, DELETE, DELETE),
            Triple(DELETE, AFTER, DELETE),
            Triple(DELETE, BEFORE, DELETE),
            Triple(DELETE, BOTH, DELETE),

            Triple(AFTER, DELETE, AFTER),
            Triple(AFTER, AFTER, DELETE),
            Triple(AFTER, BEFORE, AFTER),
            Triple(AFTER, BOTH, DELETE),

            Triple(BEFORE, DELETE, BEFORE),
            Triple(BEFORE, AFTER, BEFORE),
            Triple(BEFORE, BEFORE, DELETE),
            Triple(BEFORE, BOTH, DELETE),

            Triple(BOTH, DELETE, BOTH),
            Triple(BOTH, AFTER, BEFORE),
            Triple(BOTH, BEFORE, AFTER),
            Triple(BOTH, BOTH, DELETE)
        )

        subtractions.forEach { (a, b, c) ->
            assertEquals(c, a - b, String.format("%s - %s != %s", a, b, c))
        }
    }
}