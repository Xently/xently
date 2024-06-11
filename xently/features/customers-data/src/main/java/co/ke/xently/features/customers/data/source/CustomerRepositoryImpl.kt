package co.ke.xently.features.customers.data.source

import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.CustomerFilters
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.data.domain.error.Result
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import co.ke.xently.libraries.data.core.Link
import co.ke.xently.libraries.pagination.data.PagedResponse
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
internal class CustomerRepositoryImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val database: CustomerDatabase,
) : CustomerRepository {
    override suspend fun save(customer: Customer): Result<Unit, DataError> {
        TODO("Not yet implemented")
    }

    override suspend fun findById(id: Long): Flow<Result<Customer, DataError>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCustomers(
        url: String?,
        filters: CustomerFilters,
    ): PagedResponse<Customer> {
        val customers = List(20) {
            Customer(
                id = "${it + 1}",
                visitCount = Random.nextInt(1, 50),
                totalPoints = Random.nextInt(101, 1000),
                position = it + 1,
                placesVisitedCount = Random.nextInt(1, 10),
                links = mapOf(
                    "self" to Link(href = "https://jsonplaceholder.typicode.com/posts/${it + 1}")
                ),
            )
        }
        return PagedResponse(embedded = mapOf("views" to customers))
    }
}