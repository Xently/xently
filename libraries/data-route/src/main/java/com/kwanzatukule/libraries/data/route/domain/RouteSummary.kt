package com.kwanzatukule.libraries.data.route.domain

import kotlinx.serialization.Serializable

@Serializable
data class RouteSummary(
    val bookedOrder: Int,
    val variance: Int,
    val numberOfCustomers: Int,
    val totalRouteCustomers: Int,
    val geographicalDistance: Int,
)