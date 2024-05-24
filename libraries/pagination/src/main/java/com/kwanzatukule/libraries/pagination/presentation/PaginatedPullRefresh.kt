package com.kwanzatukule.libraries.pagination.presentation

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems

typealias IsRefreshing = Boolean
typealias RefreshLoadState = Pair<LoadState, IsRefreshing>

@OptIn(ExperimentalMaterial3Api::class)
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
    val refreshing by remember(refreshLoadState) {
        derivedStateOf {
            refreshLoadState == LoadState.Loading
                    && items.itemCount > 0
        }
    }

    val state = rememberPullToRefreshState()

    if (state.isRefreshing) {
        LaunchedEffect(true) {
            items.refresh()
        }
    }

    LaunchedEffect(refreshing) {
        if (!refreshing && state.isRefreshing) {
            state.endRefresh()
        }
    }

    val scaleFraction = if (state.isRefreshing) {
        1f
    } else {
        LinearOutSlowInEasing.transform(state.progress).coerceIn(0f, 1f)
    }

    Box(modifier = modifier.nestedScroll(state.nestedScrollConnection)) {
        preIndicatorContent(refreshLoadState to refreshing)
        PullToRefreshContainer(
            state = state,
            modifier = Modifier
                .align(alignment)
                .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
        )
    }
}
