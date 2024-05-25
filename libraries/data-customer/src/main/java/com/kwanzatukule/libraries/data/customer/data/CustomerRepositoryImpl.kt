package com.kwanzatukule.libraries.data.customer.data

import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.customer.domain.error.DataError
import com.kwanzatukule.libraries.data.customer.domain.error.Result
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random

@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: CustomerDatabase,
) : CustomerRepository {
    override fun getCustomers(url: String?, filter: Filter): PagedResponse<Customer> {
        val content = List(Random.nextInt(5, 20)) {
            Customer(
                name = "Customer $it",
                email = "customer$it@example.com",
                phone = "+2547${Random.nextLong(10000000, 99999999)}",
                id = (it + 1).toLong(),
            )
        }
        return PagedResponse(embedded = mapOf("views" to content))
        /*val refreshInterval: Duration = 5.seconds
        suspend fun save(): List<Customer> {
            Timber.i("Refreshing customers...")
            val content = httpClient.get("https://example.com")
                .body<List<Customer>>()
            delay(Random.nextLong(100, 2000))
            withContext(NonCancellable) {
                database.withTransactionFacade {
                    val entities = content.map { CustomerEntity(it) }
                    with(database.customerDao()) {
                        clear()
                        save(entities)
                    }
                }
            }
            return content
        }
        return database.customerDao()
            .getCustomers()
            .map { entities -> entities.map { it.customer } }
            .onEmpty { emit(save()) }
            .onStart {
                while (true) {
                    emit(save())
                    Timber.i("Waiting %s before another check...", refreshInterval)
                    delay(refreshInterval)
                }
            }*/
    }

    override suspend fun save(customer: Customer): Result<Unit, DataError> {
        /*val response = httpClient.post("https://example.com") {
            contentType(ContentType.Application.Json)
            setBody(customer)
        }.body<Customer>()*/
        val response = customer
        delay(Random.nextLong(100, 2000))
        return try {
            withContext(Dispatchers.IO) {
                val entity = CustomerEntity(customer = response)
                database.customerDao().save(listOf(entity))
            }
            Result.Success(Unit)
        } catch (ex: Exception) {
            if (ex is CancellationException) throw ex
            Result.Failure(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun getMyCustomer(): Customer {
        return Customer(
            id = 1,
            name = "John Doe",
            email = "john.doe@example.com",
            phone = "+2547${Random.nextLong(10000000, 99999999)}",
        )
    }
}