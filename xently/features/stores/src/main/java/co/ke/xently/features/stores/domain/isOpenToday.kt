package co.ke.xently.features.stores.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.libraries.data.core.Time
import kotlinx.datetime.DayOfWeek

fun isOpenToday(openTime: Time, closeTime: Time, currentTime: Time = Time.now()): Boolean {
    val openTimeMinutes = openTime.hour * 60 + openTime.minute
    val closeTimeMinutes = closeTime.hour * 60 + closeTime.minute
    val currentTimeMinutes = currentTime.hour * 60 + currentTime.minute

    if (openTimeMinutes > closeTimeMinutes) {
        return currentTimeMinutes in openTimeMinutes..(23 * 60 + 59) || currentTimeMinutes in 0..closeTimeMinutes
    }

    return currentTimeMinutes in openTimeMinutes..closeTimeMinutes
}

fun OpeningHour.isCurrentlyOpen(dayOfWeekToday: DayOfWeek): Boolean? {
    return if (dayOfWeek != dayOfWeekToday) {
        null
    } else {
        open && isOpenToday(
            openTime = openTime,
            closeTime = closeTime,
        )
    }
}

fun List<OpeningHour>.isCurrentlyOpen(dayOfWeekToday: DayOfWeek): Pair<OpeningHour, Boolean>? {
    var isCurrentlyOpen: Boolean? = null
    var openingHour: OpeningHour? = null
    for (hour in this) {
        when (hour.isCurrentlyOpen(dayOfWeekToday)) {
            null -> continue
            true -> return hour to true
            false -> {
                openingHour = hour
                isCurrentlyOpen = false
            }
        }
    }
    return if (isCurrentlyOpen == null || openingHour == null) {
        null
    } else {
        openingHour to isCurrentlyOpen
    }
}