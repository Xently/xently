package co.ke.xently.features.stores.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import co.ke.xently.features.stores.R
import co.ke.xently.features.stores.data.domain.Store
import co.ke.xently.features.stores.data.domain.error.ConfigurationError
import co.ke.xently.features.stores.data.domain.error.DataError
import co.ke.xently.features.stores.data.domain.error.toError
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.libraries.data.core.domain.error.RetryableError
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.pagination.ListState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import co.ke.xently.libraries.ui.pagination.asListState

@Composable
fun StoreListScreenContent(
    stores: LazyPagingItems<Store>,
    verticalArrangement: Arrangement.HorizontalOrVertical,
    modifier: Modifier = Modifier,
    emptyMessage: String = stringResource(R.string.message_no_stores_found),
    contentPadding: PaddingValues = PaddingValues(),
    storeListItem: @Composable (Store?) -> Unit,
) {
    val refreshLoadState = stores.loadState.refresh
    val isRefreshing by remember(refreshLoadState, stores.itemCount) {
        derivedStateOf { refreshLoadState == LoadState.Loading && stores.itemCount > 0 }
    }

    PullRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = stores::refresh,
        modifier = modifier.fillMaxSize(),
    ) {
        when (val state = refreshLoadState.asListState(stores.itemCount, Throwable::toError)) {
            ListState.Empty -> {
                StoreListEmptyState(
                    modifier = Modifier.matchParentSize(),
                    message = emptyMessage,
                    onClickRetry = stores::refresh,
                )
            }

            ListState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }

            ListState.Ready -> {
                StoreListLazyVerticalGrid(
                    stores = stores,
                    modifier = Modifier.matchParentSize(),
                    storeListItem = storeListItem,
                    contentPadding = contentPadding,
                    verticalArrangement = verticalArrangement,
                )
            }

            is ListState.Error -> {
                StoreListEmptyState(
                    modifier = Modifier.matchParentSize(),
                    message = state.error.asString(),
                    canRetry = state.error is RetryableError,
                    onClickRetry = stores::retry,
                ) {
                    val eventHandler = LocalEventHandler.current
                    when (state.error) {
                        ConfigurationError.ShopSelectionRequired -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = eventHandler::requestShopSelection) {
                                Text(text = stringResource(R.string.action_select_shop))
                            }
                        }

                        ConfigurationError.StoreSelectionRequired -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = eventHandler::requestStoreSelection) {
                                Text(text = stringResource(R.string.action_select_store))
                            }
                        }

                        DataError.Network.Unauthorized -> {
                            Spacer(modifier = Modifier.height(16.dp))

                            LoginAndRetryButtonsRow(onRetry = stores::retry)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}