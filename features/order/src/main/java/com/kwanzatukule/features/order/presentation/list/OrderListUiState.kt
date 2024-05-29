package com.kwanzatukule.features.order.presentation.list

import com.kwanzatukule.libraries.location.tracker.domain.Location

data class OrderListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val location: Location? = null,
)
