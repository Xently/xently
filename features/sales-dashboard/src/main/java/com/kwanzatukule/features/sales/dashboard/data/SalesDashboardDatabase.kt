package com.kwanzatukule.features.sales.dashboard.data

import com.kwanzatukule.libraries.core.data.TransactionFacadeDatabase

interface SalesDashboardDatabase : TransactionFacadeDatabase {
    fun salesDashboardItemEntityDao(): SalesDashboardItemEntityDao
}