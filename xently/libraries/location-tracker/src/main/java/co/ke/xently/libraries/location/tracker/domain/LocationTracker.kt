package co.ke.xently.libraries.location.tracker.domain

import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.Result

fun interface LocationTracker {
    suspend fun getCurrentLocation(): Result<Location, Error>
}