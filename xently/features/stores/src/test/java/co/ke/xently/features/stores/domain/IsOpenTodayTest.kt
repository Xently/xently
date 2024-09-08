package co.ke.xently.features.stores.domain

import co.ke.xently.libraries.data.core.Time
import org.junit.Assert.assertEquals
import org.junit.Test

class IsOpenTodayTest {
    @Test
    fun `should return true when store is open today 0`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 8, minute = 0),
            Time(hour = 12, minute = 0),
            Time(hour = 8, minute = 0) to true,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when store is open today 1`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 8, minute = 0),
            Time(hour = 12, minute = 0),
            Time(hour = 7, minute = 59) to false,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when store is open today 2`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 8, minute = 0),
            Time(hour = 12, minute = 0),
            Time(hour = 12, minute = 0) to true,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when store is open today 3`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 8, minute = 0),
            Time(hour = 12, minute = 0),
            Time(hour = 12, minute = 1) to false,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when store is open today 4`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 11, minute = 0),
            Time(hour = 1, minute = 0),
            Time(hour = 12, minute = 0) to true,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when store is open today 5`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 11, minute = 0),
            Time(hour = 1, minute = 0),
            Time(hour = 1, minute = 1) to false,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `should return true when store is open today 6`() {
        val (openTime, closeTime, currentTimeAndExpected) = Triple(
            Time(hour = 11, minute = 0),
            Time(hour = 1, minute = 0),
            Time(hour = 0, minute = 59) to true,
        )
        val (currentTime, expected) = currentTimeAndExpected
        val actual = isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )

        assertEquals(expected, actual)
    }
}