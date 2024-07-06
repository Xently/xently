package co.ke.xently.customer.domain

import kotlinx.serialization.Serializable

@Serializable
data class PickLocationScreen(val latitude: String? = null, val longitude: String? = null)