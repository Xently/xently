package com.kwanzatukule.libraries.pagination.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.kwanzatukule.libraries.pagination.presentation.components.PaginatedContentLazyVerticalGrid
import com.kwanzatukule.libraries.pagination.presentation.components.rememberPaginatedUiState


@Composable
fun <T : Any> PaginatedLazyVerticalGrid(
    items: LazyPagingItems<T>,
    emptyContentMessage: String,
    columns: GridCells,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    errorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
    prependErrorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
    appendErrorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
    content: LazyGridScope.() -> Unit,
) {
    PaginatedPullRefresh(
        modifier = modifier,
        items = items,
        alignment = Alignment.TopCenter,
    ) { (refreshLoadState, refreshing) ->
        when (val state = refreshLoadState.rememberPaginatedUiState(items, refreshing)) {
            PaginatedUiState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(text = emptyContentMessage)
                }
            }

            is PaginatedUiState.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    errorStateContent(state.error)
                }
            }

            PaginatedUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            PaginatedUiState.Success -> {
                PaginatedContentLazyVerticalGrid(
                    items = items,
                    columns = columns,
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement,
                    postPrependLoadingContent = content,
                    prependErrorStateContent = prependErrorStateContent,
                    appendErrorStateContent = appendErrorStateContent,
                )
            }
        }
    }
}
