package co.ke.xently.features.stores.domain


import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DistanceCalculatorTest {
    @Test
    fun `should return smallest distance unit when less than 1000`() {
        val distance = 999

        val actual = runBlocking { distance.toSmallestDistanceUnit() }

        assertEquals(
            SmallestDistanceUnit(
                "999",
                SmallestDistanceUnit.DistanceUnit.m,
            ),
            actual,
        )
    }

    @Test
    fun `should return smallest distance unit when equal 1000`() {
        val distance = 1_000

        val actual = runBlocking { distance.toSmallestDistanceUnit() }

        assertEquals(
            SmallestDistanceUnit(
                "1",
                SmallestDistanceUnit.DistanceUnit.km,
            ),
            actual,
        )
    }

    @Test
    fun `should return smallest distance unit when greater than 1000`() {
        val distance = 990_000

        val actual = runBlocking { distance.toSmallestDistanceUnit() }

        assertEquals(
            SmallestDistanceUnit(
                "990",
                SmallestDistanceUnit.DistanceUnit.km,
            ),
            actual,
        )
    }

    @Test
    fun `should return smallest distance unit when greater than 1000 with hundreds and tens portion`() {
        val distance = 1_240

        val actual = runBlocking { distance.toSmallestDistanceUnit() }

        assertEquals(
            SmallestDistanceUnit(
                "1.2",
                SmallestDistanceUnit.DistanceUnit.km,
            ),
            actual,
        )
    }
}