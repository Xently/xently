package co.ke.xently.features.stores.presentation.list.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import co.ke.xently.features.stores.presentation.utils.asUiText
import co.ke.xently.features.ui.core.presentation.LocalEventHandler
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import kotlinx.coroutines.runBlocking

@Composable
internal fun StoreListScreenContent(
    stores: LazyPagingItems<Store>,
    paddingValues: PaddingValues,
    verticalArrangement: Arrangement.HorizontalOrVertical,
    modifier: Modifier = Modifier,
    storeListItem: @Composable (Store?) -> Unit,
) {
    val refreshLoadState = stores.loadState.refresh
    val isRefreshing by remember(refreshLoadState, stores.itemCount) {
        derivedStateOf { refreshLoadState == LoadState.Loading && stores.itemCount > 0 }
    }
    PullRefreshBox(
        modifier = modifier
            .fillMaxSize()
            .padding(paddingValues),
        isRefreshing = isRefreshing,
        onRefresh = stores::refresh,
    ) {
        when {
            stores.itemCount == 0 && refreshLoadState is LoadState.NotLoading -> {
                StoreListEmptyState(
                    modifier = Modifier.matchParentSize(),
                    message = stringResource(R.string.message_no_stores_found),
                    onClickRetry = stores::refresh,
                )
            }

            stores.itemCount == 0 && refreshLoadState is LoadState.Error -> {
                val error = remember(refreshLoadState) {
                    runBlocking { refreshLoadState.error.toError() }
                }
                StoreListEmptyState(
                    modifier = Modifier.matchParentSize(),
                    message = error.asUiText().asString(),
                    canRetry = error is DataError.Network.Retryable || error is UnknownError,
                    onClickRetry = stores::retry,
                ) {
                    val eventHandler = LocalEventHandler.current
                    when (error) {
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

            stores.itemCount == 0 && refreshLoadState is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }

            else -> {
                StoreListLazyColumn(
                    stores = stores,
                    modifier = Modifier.matchParentSize(),
                    storeListItem = storeListItem,
                    verticalArrangement = verticalArrangement,
                )
            }
        }
    }
}