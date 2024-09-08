package com.kwanzatukule.features.order.data

import co.ke.xently.libraries.location.tracker.domain.DirectionNavigation
import co.ke.xently.libraries.location.tracker.domain.Location
import co.ke.xently.libraries.pagination.data.PagedResponse
import com.kwanzatukule.features.cart.data.ShoppingCartRepository
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.domain.error.Result
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.route.domain.Route
import com.kwanzatukule.libraries.data.route.domain.RouteSummary
import io.ktor.client.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.coroutineContext
import kotlin.random.Random

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    shoppingCartRepository: ShoppingCartRepository,
) : OrderRepository, ShoppingCartRepository by shoppingCartRepository {
    override suspend fun placeOrder(order: Order): Result<Unit, DataError> {
        /*val response = httpClient.post("https://example.com") {
            contentType(ContentType.Application.Json)
            setBody(customer)
        }.body<Order>()*/
        val response = order
        delay(Random.nextLong(100, 2000))
        return try {
            Result.Success<Unit, DataError>(Unit).also {
                coroutineScope {
                    launch(NonCancellable) {
                        clearShoppingCart()
                    }.invokeOnCompletion {
                        Timber.d("Order placed. Shopping cart cleared with the error %s", it)
                    }
                }
            }
        } catch (ex: Exception) {
            coroutineContext.ensureActive()
            Result.Failure(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun getOrders(url: String?, filter: Filter): PagedResponse<Order> {
        val content = List(Random.nextInt(60, 99)) {
            val route = Route(
                id = 1,
                name = "Kibera",
                description = "Kibera route description...",
                summary = RouteSummary(
                    bookedOrder = Random.nextInt(100),
                    variance = Random.nextInt(Int.MIN_VALUE, Int.MAX_VALUE),
                    numberOfCustomers = Random.nextInt(100),
                    totalRouteCustomers = Random.nextInt(100),
                    geographicalDistance = Random.nextInt(1_000, 10_000),
                ),
            )
            val customer = Customer(
                id = 1,
                name = "John Doe",
                email = "customer@example.com",
                phone = "+2547123456${Random.nextInt(10, 99)}",
                location = Location(Random.nextDouble(-1.35, -1.3), Random.nextDouble(36.7, 36.80)),
            )
            Order(
                id = "ORDER${it}",
                customer = customer,
                route = route,
                status = Order.Status.entries.random(),
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

    override suspend fun getDirectionNavigation(
        url: String?,
        filter: Filter,
    ): Result<DirectionNavigation, DataError> {
        val orders = getOrders(url, filter).getNullable()
            ?: return Result.Success(DirectionNavigation())

        val x = orders.mapNotNull {
            it.customer.location
                .takeIf { location -> location.isUsable() }
        }.sortedBy { it.averageCoordinates }
            .distinctBy { it.averageCoordinates }
        return Result.Success(
            DirectionNavigation(
                destination = x.lastOrNull(),
                waypoints = x.take(9),
            )
        )
    }

    override suspend fun getMidLocation(
        url: String?,
        filter: Filter,
    ): Result<Location?, DataError> {
        val orders = getOrders(url, filter).getNullable()
            ?: return Result.Success(null)

        val x = orders.mapNotNull {
            it.customer.location
                .takeIf { location -> location.isUsable() }
        }.sortedBy { it.averageCoordinates }

        if (x.isEmpty()) return Result.Success(null)

        return Result.Success(x[x.size / 2])
    }
}