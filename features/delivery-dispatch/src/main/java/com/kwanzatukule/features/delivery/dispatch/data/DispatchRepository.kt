package com.kwanzatukule.features.delivery.dispatch.data

import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch
import com.kwanzatukule.libraries.pagination.domain.models.PagedResponse

interface DispatchRepository {
    fun getDispatches(url: String?, filter: Filter): PagedResponse<Dispatch>
}