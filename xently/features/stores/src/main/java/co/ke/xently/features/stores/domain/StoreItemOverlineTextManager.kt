package co.ke.xently.features.stores.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.libraries.data.core.domain.DispatchersProvider
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.domain.toAndroidLocation
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

fun timeAndEmit(emissionsPerDuration: Int = 1, duration: Duration = 1.seconds): Flow<Duration> {
    return flow {
        emit(Duration.ZERO)

        var startTime = Clock.System.now()

        while (true) {
            delay(duration / emissionsPerDuration)
            val endTime = Clock.System.now()
            val timeLapse = endTime - startTime
            startTime = endTime
            emit(timeLapse)
        }
    }
}

fun flowOfDistanceAndCurrentlyOpen(
    currentLocation: Location?,
    is24hour: Boolean,
    location: Location,
    fallbackDistanceMeters: Double?,
    openingHours: List<OpeningHour>,
    dispatcher: DispatchersProvider,
) = isCurrentlyOpenFlow(
    openingHours = openingHours,
    dispatchersProvider = dispatcher,
).map { isCurrentlyOpen ->
    val distance = getDistance(
        location = location,
        dispatcher = dispatcher,
        currentLocation = currentLocation,
        fallbackDistanceMeters = fallbackDistanceMeters,
    )
    distance to isCurrentlyOpen
}.distinctUntilChanged { old, new ->
    val (distanceA, isCurrentlyOpenA) = old
    val (distanceB, isCurrentlyOpenB) = new
    val (dayOfWeekA, isOpenA, _) = isCurrentlyOpenA
    val (dayOfWeekB, isOpenB, _) = isCurrentlyOpenB

    dayOfWeekA == dayOfWeekB
            && isOpenA == isOpenB
            && distanceA == distanceB
}.map { (distance, isCurrentlyOpen) ->
    val (dayOfWeek, isOpen, operationHours) = isCurrentlyOpen

    val formattedOperationTime =
        operationHours.joinToString(separator = " • ") { (hour, _) ->
            buildString {
                append(hour.openTime.toString(is24hour))
                append(" - ")
                append(hour.closeTime.toString(is24hour))
            }
        }

    val text = buildString {
        var separator = ""
        if (distance.isNotBlank()) {
            append(distance)
            separator = " | "
        }
        if (formattedOperationTime.isNotBlank()) {
            append(separator)
            append(formattedOperationTime)
            append(" | ")
            append(dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() })
        }
    }

    isOpen to text
}.flowOn(dispatcher.default)

private suspend fun getDistance(
    currentLocation: Location?,
    location: Location,
    fallbackDistanceMeters: Double?,
    dispatcher: DispatchersProvider,
): String {
    val distanceMeters = withContext(dispatcher.default) {
        currentLocation?.toAndroidLocation()
            ?.distanceTo(location.toAndroidLocation())
    } ?: fallbackDistanceMeters
    return distanceMeters?.toSmallestDistanceUnit(dispatchersProvider = dispatcher)?.toString()
        ?: ""
}

private fun isCurrentlyOpenFlow(
    openingHours: List<OpeningHour>,
    dispatchersProvider: DispatchersProvider,
): Flow<IsCurrentlyOpen> {
    return timeAndEmit().map {
        openingHours.isCurrentlyOpen(dispatchersProvider = dispatchersProvider)
    }
}