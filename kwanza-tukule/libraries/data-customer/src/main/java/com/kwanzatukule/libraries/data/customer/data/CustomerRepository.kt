package com.kwanzatukule.libraries.data.customer.data

import co.ke.xently.libraries.pagination.domain.models.PagedResponse
import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.customer.domain.error.DataError
import com.kwanzatukule.libraries.data.customer.domain.error.Result

interface CustomerRepository {
    fun getCustomers(url: String?, filter: Filter): PagedResponse<Customer>
    suspend fun save(customer: Customer): Result<Unit, DataError>
    suspend fun getMyCustomer(): Customer
}