package co.ke.xently.customer.domain

import kotlinx.serialization.Serializable

@Serializable
data class StoreDetailScreen(
    val storeId: Long,
    val productsUrl: String,
)