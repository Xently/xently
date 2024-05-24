package com.kwanzatukule.features.customer.complaints.data

import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.features.customer.complaints.domain.error.DataError
import com.kwanzatukule.features.customer.complaints.domain.error.Result
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse

interface CustomerComplaintRepository {
    fun getCustomerComplaints(url: String?, filter: Filter): PagedResponse<CustomerComplaint>
    suspend fun save(customer: CustomerComplaint): Result<Unit, DataError>
}