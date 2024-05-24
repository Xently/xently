package com.kwanzatukule.libraries.location.tracker.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Location(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @Transient
    val name: String? = null,
) {
    fun isUsable() = (!latitude.isNaN()
            || !longitude.isNaN())

    override fun toString(): String {
        return if (!name.isNullOrBlank()) {
            name
        } else {
            coordinatesString()
        }
    }

    fun coordinatesString() = "x=$longitude,y=$latitude"
}