package co.ke.xently.features.stores.domain

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure.DurationToOperationClosure
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure.DurationToOperationStart
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure.NotOperational
import co.ke.xently.libraries.data.core.Time
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.format.DateTimeComponents
import org.junit.Test
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class OperationStartOrClosureTest {
    @Test
    fun `should return DurationToOperationStart when store is about to open for single operation hours`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 07:05:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationStart::class)
            assertThat(actual.duration)
                .isEqualTo(55.minutes)
        }
    }

    @Test
    fun `should return DurationToOperationStart when store is about to open for split operation hours`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 22, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 12:01:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationStart::class)
            assertThat(actual.duration)
                .isEqualTo(59.minutes)
        }
    }

    @Test
    fun `should return DurationToOperationStart when store is about to open for split operation hours reversed`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 16, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 16, minute = 30),
                    closeTime = Time(hour = 22, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 12:01:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationStart::class)
            assertThat(actual.duration)
                .isEqualTo(59.minutes)
        }
    }

    @Test
    fun `should return DurationToOperationStart when store is about to open for split operation hours when the closest is closed`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = false,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 16, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 16, minute = 30),
                    closeTime = Time(hour = 22, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 12:30:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationStart::class)
            assertThat(actual.duration)
                .isEqualTo(4.hours)
        }
    }

    @Test
    fun `should return DurationToOperationStart when store is about to close for single operation hours`() =
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 11:05:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationClosure::class)
            assertThat(actual.duration)
                .isEqualTo(55.minutes)
        }

    @Test
    fun `should return DurationToOperationStart when store is about to close for single operation hours when operation hours is overdue`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 12:05:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(NotOperational::class)
        }
    }

    @Test
    fun `should return DurationToOperationStart when store is about to close for split operation hours`() =
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 22, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 21:01:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationClosure::class)
            assertThat(actual.duration)
                .isEqualTo(59.minutes)
        }

    @Test
    fun `should return DurationToOperationStart when store is about to close for split operation hours reversed`() =
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 16, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 16, minute = 30),
                    closeTime = Time(hour = 22, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 15:01:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(DurationToOperationClosure::class)
            assertThat(actual.duration)
                .isEqualTo(59.minutes)
        }

    @Test
    fun `should return NotOperational when store is not open on mismatched day of week`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 16, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 16, minute = 30),
                    closeTime = Time(hour = 22, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = true,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Tue, 24 Sep 2024 12:05:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(NotOperational::class)
        }
    }

    @Test
    fun `should return NotOperational when store is not open when explicitly flagged as closed`() {
        runBlocking {
            val openingHours = listOf(
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = false,
                    openTime = Time(hour = 13, minute = 0),
                    closeTime = Time(hour = 16, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = false,
                    openTime = Time(hour = 16, minute = 30),
                    closeTime = Time(hour = 22, minute = 0)
                ),
                OpeningHour(
                    dayOfWeek = DayOfWeek.MONDAY,
                    open = false,
                    openTime = Time(hour = 8, minute = 0),
                    closeTime = Time(hour = 12, minute = 0)
                ),
            )
            val instant = Instant.parse(
                "Mon, 23 Sep 2024 12:05:30 +0300",
                DateTimeComponents.Formats.RFC_1123
            )

            val actual = openingHours.toOperationStartOrClosure(instant = instant)

            assertThat(actual)
                .isInstanceOf(NotOperational::class)
        }
    }
}