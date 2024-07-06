package co.ke.xently.business.domain

import kotlinx.serialization.Serializable

@Serializable
data class EditStoreScreen(
    val storeId: Long = -1,
    val addStoreUrl: String? = null,
)
