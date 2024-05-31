package com.kwanzatukule.features.sales.dashboard.presentation

import androidx.compose.runtime.Stable
import com.kwanzatukule.features.sales.dashboard.domain.SalesDashboardItem

@Stable
data class SalesDashboardUiState(
    val isLoading: Boolean = false,
    val content: List<SalesDashboardItem> = emptyList(),
)
