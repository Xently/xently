package co.ke.xently.features.stores.domain

import co.ke.xently.libraries.data.core.Time

fun isOpenToday(openTime: Time, closeTime: Time, currentTime: Time = Time.now()): Boolean {
    val openTimeMinutes = openTime.hour * 60 + openTime.minute
    val closeTimeMinutes = closeTime.hour * 60 + closeTime.minute
    val currentTimeMinutes = currentTime.hour * 60 + currentTime.minute

    if (openTimeMinutes > closeTimeMinutes) {
        return currentTimeMinutes in openTimeMinutes..(23 * 60 + 59) || currentTimeMinutes in 0..closeTimeMinutes
    }

    return currentTimeMinutes in openTimeMinutes..closeTimeMinutes
}