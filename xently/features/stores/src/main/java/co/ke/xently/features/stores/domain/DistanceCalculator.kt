package co.ke.xently.features.stores.domain

import co.ke.xently.libraries.data.core.DispatchersProvider
import co.ke.xently.libraries.ui.core.domain.coolFormat
import kotlinx.coroutines.withContext

typealias DistanceInMeters = Number

@Suppress("EnumEntryName")
data class SmallestDistanceUnit(val distance: String, val unit: DistanceUnit) {
    enum class DistanceUnit {
        m,
        km,
    }

    override fun toString(): String {
        return "$distance ${unit.name}"
    }
}

suspend fun DistanceInMeters.toSmallestDistanceUnit(dispatchersProvider: DispatchersProvider = DispatchersProvider.Default): SmallestDistanceUnit {
    return withContext(dispatchersProvider.default) {
        val distanceDouble = toDouble()
        val formattedDistance = distanceDouble.coolFormat(decimalPlaces = 0)
        val distance = formattedDistance.replace(
            "[a-z]+\\s*$".toRegex(RegexOption.IGNORE_CASE),
            "",
        )
        val distanceUnit = when {
            distanceDouble < 1000 -> SmallestDistanceUnit.DistanceUnit.m
            else -> SmallestDistanceUnit.DistanceUnit.km
        }
        SmallestDistanceUnit(distance = distance, unit = distanceUnit)
    }
}