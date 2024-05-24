package com.kwanzatukule.libraries.data.route.data

import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import com.kwanzatukule.libraries.data.route.domain.error.DataError
import com.kwanzatukule.libraries.data.route.domain.error.Result
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun getRouteSummary(route: Route): Flow<RouteSummary>
    fun getRoutes(url: String?, filter: Filter): PagedResponse<Route>
    suspend fun save(route: Route): Result<Unit, DataError>
}