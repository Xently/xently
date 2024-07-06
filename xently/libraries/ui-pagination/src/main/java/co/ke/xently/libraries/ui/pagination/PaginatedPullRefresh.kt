package co.ke.xently.libraries.ui.pagination

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

typealias IsRefreshing = Boolean
typealias RefreshLoadState = Pair<LoadState, IsRefreshing>

@Composable
internal fun <T : Any> PaginatedPullRefresh(
    modifier: Modifier,
    items: LazyPagingItems<T>,
    alignment: Alignment,
    preIndicatorContent: @Composable BoxScope.(RefreshLoadState) -> Unit,
) {
    val refreshLoadState by remember(items.loadState) {
        derivedStateOf {
            items.loadState.refresh
        }
    }
    val isRefreshing by remember(refreshLoadState) {
        derivedStateOf {
            refreshLoadState == LoadState.Loading
                    && items.itemCount > 0
        }
    }

    PullRefreshBox(
        modifier = modifier,
        alignment = alignment,
        isRefreshing = isRefreshing,
        onRefresh = items::refresh,
    ) { preIndicatorContent(refreshLoadState to isRefreshing) }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PullRefreshBox(
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopCenter,
    onRefresh: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    val state = rememberPullToRefreshState()

    Box(
        modifier = modifier.pullToRefresh(
            state = state,
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ),
    ) {
        content()
        val scaleFraction = {
            if (isRefreshing) 1f else
                LinearOutSlowInEasing.transform(state.distanceFraction).coerceIn(0f, 1f)
        }
        Box(
            Modifier
                .align(alignment)
                .graphicsLayer {
                    scaleX = scaleFraction()
                    scaleY = scaleFraction()
                }
        ) {
            PullToRefreshDefaults.Indicator(state = state, isRefreshing = isRefreshing)
        }
    }
}
