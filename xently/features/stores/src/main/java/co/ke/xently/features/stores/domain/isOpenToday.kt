package co.ke.xently.features.stores.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure.DurationToOperationClosure
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure.DurationToOperationStart
import co.ke.xently.features.stores.domain.DurationToOperationStartOrClosure.NotOperational
import co.ke.xently.libraries.data.core.Time
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

private fun Time.toMinutes(): Int {
    return hour * 60 + minute
}

fun isOpenToday(openTime: Time, closeTime: Time, currentTime: Time = Time.now()): Boolean {
    val openTimeMinutes = openTime.toMinutes()
    val closeTimeMinutes = closeTime.toMinutes()
    val currentTimeMinutes = currentTime.toMinutes()

    if (openTimeMinutes > closeTimeMinutes) {
        return currentTimeMinutes in openTimeMinutes..(24 * 60)
                || currentTimeMinutes in 0..closeTimeMinutes
    }

    return currentTimeMinutes in openTimeMinutes..closeTimeMinutes
}

fun OpeningHour.isCurrentlyOpen(
    dayOfWeekToday: DayOfWeek,
    currentTime: Time = Time.now(),
): Boolean? {
    return if (dayOfWeek != dayOfWeekToday) {
        null
    } else {
        open && isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
            currentTime = currentTime,
        )
    }
}

sealed interface DurationToOperationStartOrClosure {
    val duration: Duration

    data object NotOperational : DurationToOperationStartOrClosure {
        override val duration: Duration = Duration.ZERO
    }

    class DurationToOperationStart(override val duration: Duration) :
        DurationToOperationStartOrClosure

    class DurationToOperationClosure(override val duration: Duration) :
        DurationToOperationStartOrClosure
}

suspend fun List<OpeningHour>.toOperationStartOrClosure(
    instant: Instant = Clock.System.now(),
    dispatchersProvider: DispatchersProvider = DispatchersProvider.Default,
): DurationToOperationStartOrClosure {
    return withContext(dispatchersProvider.default) {
        val timeZone = TimeZone.currentSystemDefault()
        val currentDateTime = instant.toLocalDateTime(timeZone)
        val dayOfWeekToday = currentDateTime.dayOfWeek
        val currentTime = Time(
            hour = currentDateTime.hour,
            minute = currentDateTime.minute,
            utcOffset = timeZone.offsetAt(instant),
        )

        val userTime = currentTime.toMinutes()

        val responses = buildList {
            for (hour in this@toOperationStartOrClosure) {
                if (hour.dayOfWeek != dayOfWeekToday || !hour.open) {
                    continue
                }

                add(
                    async {
                        val openTime = hour.openTime.toMinutes()
                        val closeTime = hour.closeTime.toMinutes()

                        val timeToOpen: Int
                        val timeToClose: Int

                        if (openTime > closeTime) {
                            // Account for the case where the store open time is
                            // for example 11:00 and close time is 1:00
                            var ut = userTime
                            if (openTime > ut) {
                                // Account for the case where the store open time is
                                // for example 11:00 and user time is 1:00
                                ut += (24 * 60)
                            }
                            timeToOpen = openTime - ut
                            timeToClose = (24 * 60) + closeTime - ut
                        } else {
                            timeToOpen = openTime - userTime
                            timeToClose = closeTime - userTime
                        }

                        if (timeToOpen > 0) {
                            DurationToOperationStart(timeToOpen.minutes)
                        } else if (timeToClose > 0) {
                            DurationToOperationClosure(timeToClose.minutes)
                        } else {
                            NotOperational
                        }
                    }
                )
            }
        }

        responses.awaitAll().minByOrNull {
            if (it is NotOperational) {
                // Should be treated as a fallback option.
                Duration.INFINITE
            } else {
                it.duration
            }
        } ?: NotOperational
    }
}

typealias IsOpen = Boolean
typealias IsCurrentlyOpen = Triple<DayOfWeek, IsOpen, List<Pair<OpeningHour, IsOpen>>>

suspend fun List<OpeningHour>.isCurrentlyOpen(dispatchersProvider: DispatchersProvider): IsCurrentlyOpen {
    return withContext(dispatchersProvider.default) {
        val instant = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val currentDateTime = instant.toLocalDateTime(timeZone)
        val dayOfWeekToday = currentDateTime.dayOfWeek
        val currentTime = Time(
            hour = currentDateTime.hour,
            minute = currentDateTime.minute,
            utcOffset = timeZone.offsetAt(instant),
        )

        val hours = map {
            async {
                val isCurrentlyOpen = it.isCurrentlyOpen(
                    dayOfWeekToday = dayOfWeekToday,
                    currentTime = currentTime,
                )
                if (isCurrentlyOpen == null) {
                    null
                } else {
                    it to isCurrentlyOpen
                }
            }
        }.awaitAll().filterNotNull()
            .sortedBy { it.first.openTime }

        Triple(dayOfWeekToday, hours.any { it.second }, hours)
    }
}