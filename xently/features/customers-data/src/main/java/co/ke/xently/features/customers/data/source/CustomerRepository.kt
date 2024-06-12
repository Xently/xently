package co.ke.xently.features.customers.data.source

import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.CustomerFilters
import co.ke.xently.features.customers.data.domain.error.DataError
import co.ke.xently.features.customers.data.domain.error.Result
import co.ke.xently.libraries.pagination.data.PagedResponse
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun getCustomers(url: String?, filters: CustomerFilters): PagedResponse<Customer>
}
