package com.kwanzatukule.features.catalogue.domain

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
@Stable
data class Product(
    val name: String,
    val price: Int,
    val currency: String = "KES",
    val categories: List<Category> = emptyList(),
    val images: List<String> = emptyList(),
    val image: String? = images.firstOrNull(),
    val description: String? = null,
    val id: Long = -1,
    val inShoppingCart: Boolean = false,
)