package com.kwanzatukule.libraries.data.customer.domain

import com.kwanzatukule.libraries.location.tracker.domain.Location
import kotlinx.serialization.Serializable


@Serializable
data class Customer(
    val name: String,
    val email: String,
    val phone: String,
    val id: Long = -1,
    val image: String? = null,
    val location: Location = Location(),
)