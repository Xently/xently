package com.kwanzatukule.libraries.data.route.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kwanzatukule.libraries.data.route.domain.Route


@Entity(tableName = "routes")
data class RouteEntity(
    val route: Route,
    @PrimaryKey
    val id: Long = route.id,
)