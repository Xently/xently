package com.kwanzatukule.features.customer.complaints.data

import co.ke.xently.libraries.pagination.data.PagedResponse
import com.kwanzatukule.features.customer.complaints.domain.CustomerComplaint
import com.kwanzatukule.features.customer.complaints.domain.error.DataError
import com.kwanzatukule.features.customer.complaints.domain.error.Result

interface CustomerComplaintRepository {
    fun getCustomerComplaints(url: String?, filter: Filter): PagedResponse<CustomerComplaint>
    suspend fun save(customer: CustomerComplaint): Result<Unit, DataError>
}