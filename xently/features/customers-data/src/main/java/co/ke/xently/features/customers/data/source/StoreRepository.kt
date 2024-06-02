package co.ke.xently.features.customers.data.source

import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.data.domain.error.Result
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun save(customer: Customer): Result<Unit, DataError>
    suspend fun findById(id: Long): Flow<Result<Customer, DataError>>
}
