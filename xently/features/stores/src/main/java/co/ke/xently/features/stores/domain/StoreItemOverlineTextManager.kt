package co.ke.xently.features.stores.domain

import co.ke.xently.features.openinghours.data.domain.OpeningHour
import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.location.tracker.domain.toAndroidLocation
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext


private suspend fun getDistanceAndIsCurrentlyOpen(
    currentLocation: Location?,
    location: Location,
    fallbackDistanceMeters: Double?,
    openingHours: List<OpeningHour>,
    dispatcher: DispatchersProvider,
): Pair<String, IsCurrentlyOpen> = withContext(dispatcher.default) {
    val deferredDistance = async {
        val distanceMeters = currentLocation?.toAndroidLocation()
            ?.distanceTo(location.toAndroidLocation())
            ?: fallbackDistanceMeters
        distanceMeters?.toSmallestDistanceUnit(dispatchersProvider = dispatcher)?.toString()
            ?: ""
    }
    val deferredIsCurrentlyOpen = async {
        openingHours.isCurrentlyOpen(dispatchersProvider = dispatcher)
    }
    deferredDistance.await() to deferredIsCurrentlyOpen.await()
}

fun flowOfDistanceAndCurrentlyOpen(
    currentLocation: Location?,
    is24hour: Boolean,
    location: Location,
    fallbackDistanceMeters: Double?,
    openingHours: List<OpeningHour>,
    dispatcher: DispatchersProvider,
) = flow {
    while (true) {
        emit(
            getDistanceAndIsCurrentlyOpen(
                location = location,
                dispatcher = dispatcher,
                openingHours = openingHours,
                currentLocation = currentLocation,
                fallbackDistanceMeters = fallbackDistanceMeters,
            )
        )
        delay(1_000)
    }
}.distinctUntilChanged { a, b ->
    val (distanceA, isCurrentlyOpenA) = a
    val (distanceB, isCurrentlyOpenB) = b
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