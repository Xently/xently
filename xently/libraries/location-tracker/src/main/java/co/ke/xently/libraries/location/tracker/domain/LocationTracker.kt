package co.ke.xently.libraries.location.tracker.domain

import co.ke.xently.libraries.location.tracker.domain.error.Error
import co.ke.xently.libraries.location.tracker.domain.error.Result
import kotlinx.coroutines.flow.Flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

enum class LocationPriority(val value: Int, val interval: Duration = 1.seconds) {
    HIGH_ACCURACY(100),
    BALANCED_POWER_ACCURACY(102),
    LOW_POWER(104),
    PASSIVE(105),
}

enum class MissingPermissionBehaviour {
    CLOSE,
    REPEAT_CHECK,
}

interface LocationTracker {
    suspend fun getCurrentLocation(): Result<Location, Error>
    fun observeLocation(
        interval: Duration? = null,
        priority: LocationPriority = LocationPriority.BALANCED_POWER_ACCURACY,
        permissionBehaviour: MissingPermissionBehaviour = MissingPermissionBehaviour.CLOSE,
    ): Flow<Location>
}