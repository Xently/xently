package com.kwanzatukule.libraries.pagination.presentation

import androidx.compose.runtime.Immutable

@Immutable
sealed class PaginatedUiState {
    @Immutable
    data object Loading : PaginatedUiState()

    @Immutable
    data object Empty : PaginatedUiState()

    @Immutable
    data object Success : PaginatedUiState()

    @Immutable
    data class Error(val error: Throwable) : PaginatedUiState()
}