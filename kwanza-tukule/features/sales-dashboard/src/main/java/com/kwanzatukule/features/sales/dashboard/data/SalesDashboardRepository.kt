package com.kwanzatukule.features.sales.dashboard.data

import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem
import kotlinx.coroutines.flow.Flow

interface SalesDashboardRepository {
    fun getSalesDashboardContent(): Flow<List<SalesDashboardItem>>
}
