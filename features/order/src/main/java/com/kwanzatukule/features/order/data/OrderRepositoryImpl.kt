package com.kwanzatukule.features.order.data

import com.kwanzatukule.features.cart.data.ShoppingCartDatabase
import com.kwanzatukule.features.order.domain.Order
import com.kwanzatukule.features.order.domain.error.DataError
import com.kwanzatukule.features.order.domain.error.Result
import io.ktor.client.HttpClient
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

@Singleton
class OrderRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: ShoppingCartDatabase,
) : OrderRepository {
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
                        database.shoppingCartItemDao().deleteAll()
                    }.invokeOnCompletion {
                        Timber.d("Order placed. Shopping cart cleared with the error %s", it)
                    }
                }
            }
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Result.Failure(DataError.Network.UNKNOWN)
        }
    }
}