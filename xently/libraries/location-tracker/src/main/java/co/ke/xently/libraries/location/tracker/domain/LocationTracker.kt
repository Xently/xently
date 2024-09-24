package co.ke.xently.libraries.location.tracker.domain

import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.Result
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class LocationPriority(val value: Int, val interval: Duration) {
    HIGH_ACCURACY(100, 10.seconds),
    BALANCED_POWER_ACCURACY(102, 15.seconds),
    LOW_POWER(104, 60.seconds),
    PASSIVE(105, 30.seconds),
}

enum class MissingPermissionBehaviour {
    CLOSE,
    REPEAT_CHECK,
}

interface LocationTracker {
    suspend fun getCurrentLocation(): Result<Location, Error>
    fun observeLocation(
        interval: Duration? = null,
        minimumEmissionDistanceMeters: Int? = null,
        priority: LocationPriority = LocationPriority.BALANCED_POWER_ACCURACY,
        permissionBehaviour: MissingPermissionBehaviour = MissingPermissionBehaviour.CLOSE,
    ): Flow<Location>
}