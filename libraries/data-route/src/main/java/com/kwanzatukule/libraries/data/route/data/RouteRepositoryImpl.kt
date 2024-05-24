package com.kwanzatukule.libraries.data.route.data

import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import com.kwanzatukule.libraries.data.route.domain.error.DataError
import com.kwanzatukule.libraries.data.route.domain.error.Result
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@Singleton
class RouteRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: RouteDatabase,
) : RouteRepository {
    override fun getRouteSummary(route: Route): Flow<RouteSummary> {
        val refreshInterval: Duration = 5.seconds
        suspend fun save(): RouteSummary {
            Timber.i("Refreshing sales dashboard...")
            /*val content = httpClient.get("https://example.com")
                .body<List<RouteSummary>>()*/
            delay(Random.nextLong(100, 2000))
            val content = route.summary
            withContext(NonCancellable) {
                val entity = RouteSummaryEntity(
                    id = route.id,
                    bookedOrder = content.bookedOrder,
                    variance = content.variance,
                    numberOfCustomers = content.numberOfCustomers,
                    totalRouteCustomers = content.totalRouteCustomers,
                    geographicalDistance = content.geographicalDistance,
                )
                database.routeSummaryDao().save(entity)
            }
            return content
        }
        return database.routeSummaryDao()
            .getRouteSummary(route.id)
            .map {
                if (it == null) {
                    route.summary
                } else {
                    RouteSummary(
                        bookedOrder = it.bookedOrder,
                        variance = it.variance,
                        numberOfCustomers = it.numberOfCustomers,
                        totalRouteCustomers = it.totalRouteCustomers,
                        geographicalDistance = it.geographicalDistance,
                    )
                }
            }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    emit(save())
                    Timber.i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }
    }

    override fun getRoutes(url: String?, filter: Filter): PagedResponse<Route> {
        val content = List(Random.nextInt(5, 20)) {
            Route(
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
        }
        return PagedResponse(embedded = mapOf("views" to content))
        /*val refreshInterval: Duration = 5.seconds
        suspend fun save(): List<Route> {
            Timber.i("Refreshing routes...")
            val content = httpClient.get("https://example.com")
                .body<List<Route>>()
            delay(Random.nextLong(100, 2000))
            withContext(NonCancellable) {
                database.withTransactionFacade {
                    val entities = content.map { RouteEntity(it) }
                    with(database.routeDao()) {
                        clear()
                        save(entities)
                    }
                }
            }
            return content
        }
        return database.routeDao()
            .getRoutes()
            .map { entities -> entities.map { it.route } }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    emit(save())
                    Timber.i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }*/
    }

    override suspend fun save(route: Route): Result<Unit, DataError> {
        /*val response = httpClient.post("https://example.com") {
            contentType(ContentType.Application.Json)
            setBody(route)
        }.body<Route>()*/
        val response = route
        delay(Random.nextLong(100, 2000))
        return try {
            withContext(Dispatchers.IO) {
                val entity = RouteEntity(route = response)
                database.routeDao().save(listOf(entity))
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Result.Failure(DataError.Network.UNKNOWN)
        }
    }
}