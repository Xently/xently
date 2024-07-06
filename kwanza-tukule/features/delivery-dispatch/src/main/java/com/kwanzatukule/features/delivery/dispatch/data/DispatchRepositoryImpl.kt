package com.kwanzatukule.features.delivery.dispatch.data

import co.ke.xently.libraries.pagination.data.PagedResponse
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.features.delivery.dispatch.domain.Driver
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import io.ktor.client.HttpClient
import kotlinx.datetime.Clock
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class DispatchRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
) : DispatchRepository {
    override fun getDispatches(url: String?, filter: Filter): PagedResponse<Dispatch> {
        val content = List(Random.nextInt(60, 99)) {
            Dispatch(
                id = "ABCDEF${it + 1}",
                date = Clock.System.now(),
                driver = Driver(name = "John Doe"),
                status = Dispatch.Status.entries.random(),
                route = Route(
                    name = "Route $it",
                    description = "Route $it description",
                    id = (it + 1).toLong(),
                    summary = RouteSummary(
                        bookedOrder = Random.nextInt(100),
                        variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                        numberOfCustomers = Random.nextInt(100),
                        totalRouteCustomers = Random.nextInt(100),
                        geographicalDistance = Random.nextInt(1_000, 10_000),
                    ),
                )
            )
        }.run {
            if (filter.status == null) {
                this
            } else {
                filter { it.status == filter.status }
            }
        }
        return PagedResponse(embedded = mapOf("views" to content))
    }
}