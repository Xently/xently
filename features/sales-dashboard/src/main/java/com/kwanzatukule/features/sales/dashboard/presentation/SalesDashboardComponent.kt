package com.kwanzatukule.features.sales.dashboard.presentation

import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem

interface SalesDashboardComponent {
    val uiState: Value<SalesDashboardUiState> get() = throw NotImplementedError()

    fun onItemClicked(dashboardItem: SalesDashboardItem)

    data class Fake(val state: SalesDashboardUiState) : SalesDashboardComponent {
        override val uiState = MutableValue(state)
        override fun onItemClicked(dashboardItem: SalesDashboardItem) {}
    }
}
