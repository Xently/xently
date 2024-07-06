package co.ke.xently.business.domain

import kotlinx.serialization.Serializable

@Serializable
data class EditProductScreen(val productId: Long = -1)