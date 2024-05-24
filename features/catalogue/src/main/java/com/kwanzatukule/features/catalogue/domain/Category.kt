package com.kwanzatukule.features.catalogue.domain

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val name: String,
    val image: String? = null,
    val description: String? = null,
    val id: Long = -1,
)