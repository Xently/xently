package com.kwanzatukule.libraries.data.route.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "route_summary")
data class RouteSummaryEntity(
    @PrimaryKey
    val id: Long,
    val bookedOrder: Int,
    val variance: Int,
    val numberOfCustomers: Int,
    val totalRouteCustomers: Int,
    val geographicalDistance: Int,
)
