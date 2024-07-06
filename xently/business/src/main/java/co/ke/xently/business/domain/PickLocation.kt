package co.ke.xently.business.domain

import kotlinx.serialization.Serializable

@Serializable
data class PickLocation(val latitude: String? = null, val longitude: String? = null)