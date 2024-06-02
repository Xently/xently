package co.ke.xently.features.customers.data.source

import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.data.domain.error.Result
import co.ke.xently.features.customers.data.source.local.CustomerDatabase
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

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
}