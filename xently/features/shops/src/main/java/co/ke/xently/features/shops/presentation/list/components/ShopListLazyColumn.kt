package co.ke.xently.features.shops.presentation.list.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import co.ke.xently.features.shops.R
import co.ke.xently.features.shops.data.domain.Shop
import co.ke.xently.features.shops.data.domain.error.Error
import co.ke.xently.features.shops.data.domain.error.toShopError
import co.ke.xently.features.shops.presentation.utils.asUiText
import kotlinx.coroutines.runBlocking
import co.ke.xently.features.shops.data.domain.error.DataError as ShopDataError

@Composable
internal fun ShopListLazyColumn(
    shops: LazyPagingItems<Shop>,
    modifier: Modifier = Modifier,
    onShopSelected: (Shop) -> Unit,
    onClickEditShop: (Shop) -> Unit,
    onClickConfirmDelete: (Shop) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (val loadState = shops.loadState.refresh) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item(
                    key = "Refresh Loading",
                    contentType = "Refresh Loading",
                ) {
                    Text(
                        text = "Waiting for items to load from the backend",
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Refresh Error",
                    contentType = "Refresh Error",
                ) {
                    val error = remember(loadState.error) {
                        runBlocking { loadState.error.toShopError() }
                    }
                    ShopListErrorContent(
                        error = error,
                        onClickRetry = shops::retry,
                    )
                }
            }
        }

        when (val loadState = shops.loadState.prepend) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item(
                    key = "Prepend Loading",
                    contentType = "Prepend Loading",
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Prepend Error",
                    contentType = "Prepend Error",
                ) {
                    val error = remember(loadState.error) {
                        runBlocking { loadState.error.toShopError() }
                    }
                    ShopListErrorContent(
                        error = error,
                        onClickRetry = shops::retry,
                    )
                }
            }
        }

        items(
            count = shops.itemCount,
            key = shops.itemKey { it.id },
            contentType = shops.itemContentType { "Shops" },
        ) { index ->
            val shop = shops[index]

            if (shop != null) {
                ShopListItem(
                    shop = shop,
                    onClickUpdate = { onClickEditShop(shop) },
                    onClickConfirmDelete = { onClickConfirmDelete(shop) },
                    modifier = Modifier.clickable {
                        onShopSelected(shop)
                    },
                )
            } else {
                ShopListItem(
                    shop = Shop.DEFAULT,
                    isLoading = true,
                    onClickUpdate = {},
                    onClickConfirmDelete = {},
                )
            }
        }

        when (val loadState = shops.loadState.append) {
            is LoadState.NotLoading -> Unit
            LoadState.Loading -> {
                item(
                    key = "Append Loading",
                    contentType = "Append Loading",
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }

            is LoadState.Error -> {
                item(
                    key = "Append Error",
                    contentType = "Append Error",
                ) {
                    val error = remember(loadState.error) {
                        runBlocking { loadState.error.toShopError() }
                    }
                    ShopListErrorContent(
                        error = error,
                        onClickRetry = shops::retry,
                    )
                }
            }
        }
    }
}

@Composable
private fun ShopListErrorContent(error: Error, onClickRetry: () -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = error.asUiText().asString(),
            modifier = Modifier.weight(1f),
        )
        if (error is ShopDataError.Network.Retryable) {
            Button(onClick = onClickRetry) {
                Text(text = stringResource(R.string.action_retry))
            }
        }
    }
}