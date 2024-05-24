package com.kwanzatukule.libraries.location.tracker.domain

import com.kwanzatukule.libraries.location.tracker.domain.error.Error
import com.kwanzatukule.libraries.location.tracker.domain.error.Result

fun interface LocationTracker {
    suspend fun getCurrentLocation(): Result<Location, Error>
}