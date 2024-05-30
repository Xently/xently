package com.kwanzatukule.features.sales.dashboard.data

interface SalesDashboardDatabase : co.ke.xently.libraries.data.local.TransactionFacadeDatabase {
    fun salesDashboardItemEntityDao(): SalesDashboardItemEntityDao
}