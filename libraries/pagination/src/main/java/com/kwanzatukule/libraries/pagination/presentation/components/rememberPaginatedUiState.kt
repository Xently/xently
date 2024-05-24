package com.kwanzatukule.libraries.pagination.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.kwanzatukule.libraries.pagination.presentation.PaginatedUiState

@Composable
internal fun <T : Any> LoadState.rememberPaginatedUiState(
    items: LazyPagingItems<T>,
    isRefreshing: Boolean,
): PaginatedUiState {
    val shouldShowEmpty by remember(this, items.itemCount) {
        derivedStateOf {
            this is LoadState.NotLoading
                    && items.itemCount == 0
        }
    }

    return remember(shouldShowEmpty, this, isRefreshing) {
        if (shouldShowEmpty) {
            PaginatedUiState.Empty
        } else if (this is LoadState.Error) {
            PaginatedUiState.Error(error)
        } else if (!isRefreshing && this == LoadState.Loading) {
            PaginatedUiState.Loading
        } else {
            PaginatedUiState.Success
        }
    }
}