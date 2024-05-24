package com.kwanzatukule.features.customer.home.data

import kotlinx.serialization.Serializable

@Serializable
data class Advert(
    val title: String,
    val subtitle: String? = null,
    val image: String? = null,
    val headline: String? = null,
)