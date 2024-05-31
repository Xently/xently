package co.ke.xently.libraries.pagination.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

@Composable
internal fun <T : Any> PaginatedContentLazyColumn(
    items: LazyPagingItems<T>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    state: LazyListState = rememberLazyListState(),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    prePrependLoadingContent: LazyListScope.() -> Unit = {},
    postPrependLoadingContent: LazyListScope.() -> Unit,
    prependErrorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
    appendErrorStateContent: @Composable ColumnScope.(Throwable) -> Unit,
) {
    LazyColumn(
        state = state,
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