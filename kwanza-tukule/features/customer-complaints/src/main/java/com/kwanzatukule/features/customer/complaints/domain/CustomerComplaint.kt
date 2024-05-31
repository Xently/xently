package com.kwanzatukule.features.customer.complaints.domain

import kotlinx.serialization.Serializable


@Serializable
data class CustomerComplaint(
    val name: String,
    val email: String,
    val phone: String,
    val id: Long = -1,
)