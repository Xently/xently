package com.kwanzatukule.features.customer.complaints.presentation.list

import androidx.compose.runtime.Stable

@Stable
data class CustomerComplaintListUiState(
    val query: String = "",
    val isLoading: Boolean = false,
)
