package co.ke.xently.features.products.presentation.list.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
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
import co.ke.xently.features.products.R
import co.ke.xently.features.products.data.domain.Product
import co.ke.xently.features.products.data.domain.error.ConfigurationError
import co.ke.xently.features.products.data.domain.error.DataError
import co.ke.xently.features.products.data.domain.error.toError
import co.ke.xently.features.ui.core.presentation.components.LoginAndRetryButtonsRow
import co.ke.xently.libraries.data.core.RetryableError
import co.ke.xently.libraries.ui.core.asString
import co.ke.xently.libraries.ui.pagination.ListState
import co.ke.xently.libraries.ui.pagination.PullRefreshBox
import co.ke.xently.libraries.ui.pagination.asListState

@Composable
internal fun ProductListContent(
    products: LazyPagingItems<Product>,
    modifier: Modifier = Modifier,
    onClickSelectShop: () -> Unit,
    onClickSelectStore: () -> Unit,
    extraPrependContent: LazyListScope.() -> Unit = {},
    productListItem: @Composable LazyItemScope.(Product?) -> Unit,
) {
    val refreshLoadState = products.loadState.refresh
    val isRefreshing by remember(refreshLoadState, products.itemCount) {
        derivedStateOf {
            refreshLoadState == LoadState.Loading
                    && products.itemCount > 0
        }
    }
    PullRefreshBox(
        modifier = modifier,
        isRefreshing = isRefreshing,
        onRefresh = products::refresh,
    ) {
        when (val listState =
            refreshLoadState.asListState(products.itemCount, Throwable::toError)) {
            ListState.Empty -> {
                ProductListEmptyState(
                    modifier = Modifier.matchParentSize(),
                    message = stringResource(R.string.message_no_products_found),
                    onClickRetry = products::refresh,
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
                ProductListLazyColumn(
                    products = products,
                    modifier = Modifier.matchParentSize(),
                    productListItem = productListItem,
                    extraPrependContent = extraPrependContent,
                )
            }

            is ListState.Error -> {
                ProductListEmptyState(
                    modifier = Modifier.matchParentSize(),
                    message = listState.error.asString(),
                    canRetry = listState.error is RetryableError,
                    onClickRetry = products::retry,
                ) {
                    when (listState.error) {
                        ConfigurationError.ShopSelectionRequired -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onClickSelectShop) {
                                Text(text = stringResource(R.string.action_select_shop))
                            }
                        }

                        ConfigurationError.StoreSelectionRequired -> {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onClickSelectStore) {
                                Text(text = stringResource(R.string.action_select_store))
                            }
                        }

                        DataError.Network.Unauthorized -> {
                            Spacer(modifier = Modifier.height(16.dp))

                            LoginAndRetryButtonsRow(onRetry = products::retry)
                        }

                        else -> Unit
                    }
                }
            }
        }
    }
}