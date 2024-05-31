package com.kwanzatukule.libraries.data.route.data

import co.ke.xently.libraries.pagination.domain.models.PagedResponse
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import com.kwanzatukule.libraries.data.route.domain.error.DataError
import com.kwanzatukule.libraries.data.route.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface RouteRepository {
    fun getRouteSummary(route: Route): Flow<RouteSummary>
    fun getRoutes(url: String?, filter: Filter): PagedResponse<Route>
    suspend fun save(route: Route): Result<Unit, DataError>
}