package co.ke.xently.features.stores.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.libraries.data.core.Time
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.offsetAt
import kotlinx.datetime.toLocalDateTime

fun isOpenToday(openTime: Time, closeTime: Time, currentTime: Time = Time.now()): Boolean {
    val openTimeMinutes = openTime.hour * 60 + openTime.minute
    val closeTimeMinutes = closeTime.hour * 60 + closeTime.minute
    val currentTimeMinutes = currentTime.hour * 60 + currentTime.minute

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

typealias IsOpen = Boolean
typealias IsCurrentlyOpen = Triple<DayOfWeek, IsOpen, List<Pair<OpeningHour, IsOpen>>>

suspend fun List<OpeningHour>.isCurrentlyOpen(): IsCurrentlyOpen {
    return withContext(Dispatchers.Default) {
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