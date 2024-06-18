package co.ke.xently.features.customers.data.source

import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.CustomerFilters
import co.ke.xently.libraries.pagination.data.PagedResponse

interface CustomerRepository {
    suspend fun getCustomers(url: String, filters: CustomerFilters): PagedResponse<Customer>
}
