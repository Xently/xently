package com.kwanzatukule.features.delivery.dispatch.data

import co.ke.xently.libraries.pagination.data.PagedResponse
import com.kwanzatukule.features.delivery.dispatch.domain.Dispatch

interface DispatchRepository {
    fun getDispatches(url: String?, filter: Filter): PagedResponse<Dispatch>
}