package com.kwanzatukule.features.order.domain

import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val route: Route,
    val customer: Customer,
)
