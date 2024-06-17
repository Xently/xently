package co.ke.xently.business.domain

import kotlinx.serialization.Serializable

@Serializable
data class PickLocation(val shopId: Long = -1, val storeId: Long = -1)