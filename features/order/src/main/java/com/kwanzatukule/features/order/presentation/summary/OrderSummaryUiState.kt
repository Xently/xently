package com.kwanzatukule.features.order.presentation.summary

import androidx.compose.runtime.Stable
import com.kwanzatukule.features.order.domain.Order

@Stable
data class OrderSummaryUiState(
    val order: Order,
    val isLoading: Boolean = false,
)
