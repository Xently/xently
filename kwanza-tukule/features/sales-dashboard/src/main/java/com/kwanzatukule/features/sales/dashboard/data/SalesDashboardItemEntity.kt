package com.kwanzatukule.features.sales.dashboard.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem

@Entity(tableName = "sales_dashboard_items")
data class SalesDashboardItemEntity(
    val item: SalesDashboardItem,
    @PrimaryKey
    val id: String = item.name,
)
