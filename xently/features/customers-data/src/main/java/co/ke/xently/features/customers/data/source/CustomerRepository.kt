package co.ke.xently.features.customers.data.source

import androidx.paging.PagingData
import co.ke.xently.features.customers.data.domain.Customer
import co.ke.xently.features.customers.data.domain.CustomerFilters
import kotlinx.coroutines.flow.Flow

interface CustomerRepository {
    suspend fun getCustomersUrl(): String
    fun getCustomers(url: String, filters: CustomerFilters): Flow<PagingData<Customer>>
}
