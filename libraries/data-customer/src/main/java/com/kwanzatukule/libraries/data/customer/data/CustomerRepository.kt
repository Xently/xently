package com.kwanzatukule.libraries.data.customer.data

import com.kwanzatukule.libraries.data.customer.domain.Customer
import com.kwanzatukule.libraries.data.customer.domain.error.DataError
import com.kwanzatukule.libraries.data.customer.domain.error.Result
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse

interface CustomerRepository {
    fun getCustomers(url: String?, filter: Filter): PagedResponse<Customer>
    suspend fun save(customer: Customer): Result<Unit, DataError>
    suspend fun getMyCustomer(): Customer
}