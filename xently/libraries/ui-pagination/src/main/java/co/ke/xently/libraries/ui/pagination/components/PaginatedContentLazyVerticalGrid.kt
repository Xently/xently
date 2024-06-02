package co.ke.xently.libraries.ui.pagination.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
internal fun <T : Any> PaginatedContentLazyVerticalGrid(
    items: LazyPagingItems<T>,
    columns: GridCells,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyGridState = rememberLazyGridState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    prePrependLoadingContent: LazyGridScope.() -> Unit = {},
    postPrependLoadingContent: LazyGridScope.() -> Unit,
    prependErrorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
    appendErrorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
) {
    LazyVerticalGrid(
        state = state,
        columns = columns,
        contentPadding = contentPadding,
        verticalArrangement = verticalArrangement,
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        prePrependLoadingContent()

        val prependLoadState = items.loadState.prepend
        if (prependLoadState is LoadState.Error) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    prependErrorStateContent(prependLoadState.error)
                }
            }
        } else if (prependLoadState == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }

        postPrependLoadingContent()

        val appendLoadState = items.loadState.append
        if (appendLoadState is LoadState.Error) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    appendErrorStateContent(appendLoadState.error)
                }
            }
        } else if (appendLoadState == LoadState.Loading) {
            item {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }
    }
}