package co.ke.xently.features.stores.domain

import co.ke.xently.libraries.ui.core.domain.coolFormat

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

fun DistanceInMeters.toSmallestDistanceUnit(): SmallestDistanceUnit {

    val distanceDouble = toDouble()
    val distance = distanceDouble.coolFormat().replace(
        "[a-z]".toRegex(RegexOption.IGNORE_CASE),
        "",
    )
    val distanceUnit = when {
        distanceDouble < 1000 -> SmallestDistanceUnit.DistanceUnit.m
        else -> SmallestDistanceUnit.DistanceUnit.km
    }
    return SmallestDistanceUnit(distance = distance, unit = distanceUnit)
}