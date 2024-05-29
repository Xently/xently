package com.kwanzatukule.libraries.data.route.domain

import kotlinx.serialization.Serializable


@Serializable
data class Route(
    val name: String,
    val description: String,
    val id: Long = -1,
    val summary: RouteSummary?,
)